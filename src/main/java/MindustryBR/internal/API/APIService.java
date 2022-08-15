package MindustryBR.internal.API;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import arc.util.Log;
import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.net.Administration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import static MindustryBR.Discord.Commands.GameInfo.stats;
import static MindustryBR.Main.*;
import static MindustryBR.Mindustry.Events.worldLoad.started;
import static mindustry.Vars.netServer;
import static mindustry.Vars.state;
import static spark.Spark.*;

public class APIService {
    public static void startWebServer() {
        if (!config.has("API") || config.getJSONObject("API").isEmpty() || !config.getJSONObject("API").has("port")) {
            Log.info("[MindustryBR] API: Porta não definida");
            return;
        }

        int port = config.getJSONObject("API").getInt("port") > 0 ? config.getJSONObject("API").getInt("port") : 8080;

        port(port);

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
            res.header("Access-Control-Allow-Credentials", "true");

            if(req.headers("Authorization") != null && Objects.equals(req.headers("Authorization"), config.getJSONObject("API").getString("auth")) && !Objects.equals(config.getJSONObject("API").getString("auth"), "placeholder")) {
                if (config.getJSONObject("API").getBoolean("log")) Log.info("[MindustryBR] API: Autorizado");
            } else {
                res.status(401);
                res.body("Unauthorized");
                res.type("text/plain");
                halt();
            }
        });

        get("/", (req, res) -> {
            try {
                res.type("application/json");
                JSONObject resObj = new JSONObject();
                JSONObject stateObj = new JSONObject();
                JSONObject mapObj = new JSONObject();
                JSONObject statsObj = new JSONObject();
                JSONArray playersArray = new JSONArray();

                mapObj.put("name", state.map.name().isEmpty() ? "unknown" : state.map.name())
                        .put("desc", state.map.description().isEmpty() ? "unknown" : state.map.description())
                        .put("size", state.map.height + "x" + state.map.width)
                        .put("author", state.map.author().isEmpty() ? "unknown" : state.map.author());

                statsObj.put("unitsBuilt", stats.unitsBuilt)
                        .put("unitsDestroyed", stats.unitsDestroyed)
                        .put("buildingsConstructed", stats.buildingsConstructed)
                        .put("buildingsDeconstructed", stats.buildingsDeconstructed)
                        .put("buildingsDestroyed", stats.buildingsDestroyed);

                stateObj.put("pause", state.isPaused())
                        .put("started", started)
                        .put("map", mapObj)
                        .put("stats", statsObj);

                Groups.player.forEach(p -> playersArray.put(p.uuid()));

                resObj.put("players", playersArray)
                        .put("state", stateObj)
                        .put("discord_bot", Bot.logged);

                res.status(200);
                return resObj.toString(4);
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
            }
        });

        get("/players", (req, res) -> {
            try {
                res.type("application/json");
                JSONArray playersArray = new JSONArray();

                Groups.player.forEach(p -> {
                    JSONArray historyArray = new JSONArray();
                    LimitedQueue<BaseEntry> history = playerHistory.get(p.uuid());

                    if (history != null && !history.isEmpty()) {
                        if (history.isOverflown()) historyArray.put("... historico muito grande");
                        for (BaseEntry historyEntry : history)
                            historyArray.put(Strings.stripColors(historyEntry.getMessage(false)));
                    } else historyArray.put("~ sem historico");

                    JSONObject tmpPlayer = new JSONObject();
                    tmpPlayer.put("id", p.uuid())
                            .put("name", p.name)
                            .put("previousNames", new JSONArray(p.getInfo().names.toArray()))
                            .put("country", knownIPs.get(p.ip()))
                            .put("vip", (playersDB.has(p.uuid()) ? playersDB.getJSONObject(p.uuid()) : addPlayerAccount(p.uuid(), null)).getJSONObject("vip"))
                            .put("history", historyArray);
                });

                res.status(200);
                return playersArray.toString(4);
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
            }
        });

        path("/player/:uuid", () -> {
            // GET /player/:uuid/
            get("/", (req, res) -> {
                try {
                    JSONObject resObj = new JSONObject();

                    if (req.params("uuid") == null || !req.params("uuid").endsWith("==")) {
                        res.status(400);
                        return "{\"error\": \"Invalid UUID\"}";
                    }

                    if (netServer.admins.findByName(req.params("uuid")) == null || netServer.admins.findByName(req.params("uuid")).first() == null) {
                        res.status(404);
                        return "{\"error\": \"Player Not Found\"}";
                    }

                    Administration.PlayerInfo playerInfo = netServer.admins.findByName(req.params("uuid")).first();

                    resObj.put("name", playerInfo.lastName)
                            .put("uuid", playerInfo.id)
                            .put("country", knownIPs.get(playerInfo.lastIP))
                            .put("vip", (playersDB.has(playerInfo.id) ? playersDB.getJSONObject(playerInfo.id) : addPlayerAccount(playerInfo.id, null)).getJSONObject("vip"));

                    res.status(200);
                    return resObj.toString(4);
                } catch (Exception e) {
                    res.status(500);
                    return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
                }
            });

            get("/history", (req, res) -> {
                try {
                    JSONArray historyArray = new JSONArray();
                    LimitedQueue<BaseEntry> history = playerHistory.get(req.params("uuid"));

                    if (history != null && !history.isEmpty()) {
                        if (history.isOverflown()) historyArray.put("... historico muito grande");
                        for (BaseEntry historyEntry : history)
                            historyArray.put(Strings.stripColors(historyEntry.getMessage(false)));
                    } else historyArray.put("~ sem historico");

                    res.status(200);
                    return historyArray.toString(4);
                } catch (Exception e) {
                    res.status(500);
                    return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
                }
            });

            /// TODO: Implementar o metodo PUT para modificar um player
            // PUT /player/:uuid/
            put("/", (req, res) -> {
                JSONObject resObj = new JSONObject();

                res.status(201);
                return resObj.toString(4);
            });

            /// TODO: Implementar o metodo DELETE para deletar um player
            // DELETE /player/:uuid/
            delete("/", (req, res) -> {
                JSONObject resObj = new JSONObject();

                res.status(200);
                return resObj.toString(4);
            });
        });
    }
}

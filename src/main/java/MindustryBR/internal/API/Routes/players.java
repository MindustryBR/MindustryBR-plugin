package MindustryBR.internal.API.Routes;

import MindustryBR.internal.Classes.API.BaseRoute;
import MindustryBR.internal.Classes.History.LimitedQueue;
import MindustryBR.internal.Classes.History.entry.BaseEntry;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.HaltException;
import spark.Request;
import spark.Response;

import static MindustryBR.Main.*;
import static mindustry.Vars.netServer;

public class players extends BaseRoute {
    public static String get(Request req, Response res) {
        try {
            res.type("application/json");
            JSONObject playersObj = new JSONObject();

            playersDB.keys().forEachRemaining(keyStr -> {
                JSONObject tmpObj = playersDB.getJSONObject(keyStr);

                if (Groups.player.contains((p) -> p.uuid().equals(keyStr))) {
                    Player p = Groups.player.find((p2) -> p2.uuid().equals(keyStr));
                    JSONArray historyArray = new JSONArray();
                    LimitedQueue<BaseEntry> history = playerHistory.get(p.uuid());

                    if (history != null && !history.isEmpty()) {
                        if (history.isOverflown()) historyArray.put("... historico muito grande");
                        for (BaseEntry historyEntry : history)
                            historyArray.put(Strings.stripColors(historyEntry.getMessage(false)));
                    } else historyArray.put("~ sem historico");

                    tmpObj.put("name", p.name())
                            .put("previousNames", new JSONArray(p.getInfo().names.toArray()))
                            .put("country", knownIPtoCountry.get(p.ip()))
                            .put("online", true);

                    if (req.queryParams("history") != null && req.queryParams("history").equals("true")) tmpObj.put("history", historyArray);

                } else {
                    @Nullable
                    Administration.PlayerInfo p = netServer.admins.findByName(keyStr).size > 0? netServer.admins.findByName(keyStr).first() : null;

                    if (p == null) return;

                    JSONArray historyArray = new JSONArray();
                    LimitedQueue<BaseEntry> history = playerHistory.get(p.id);

                    if (history != null && !history.isEmpty()) {
                        if (history.isOverflown()) historyArray.put("... historico muito grande");
                        for (BaseEntry historyEntry : history)
                            historyArray.put(Strings.stripColors(historyEntry.getMessage(false)));
                    } else historyArray.put("~ sem historico");

                    tmpObj.put("name", p.lastName)
                            .put("previousNames", new JSONArray(p.names.toArray()))
                            .put("country", knownIPtoCountry.get(p.lastIP))
                            .put("online", false);

                    if (req.queryParams("history") != null && req.queryParams("history").equals("true")) tmpObj.put("history", historyArray);
                }

                playersObj.put(keyStr, tmpObj);
            });

            /*
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
                        .put("country", knownIPtoCountry.get(p.ip()))
                        .put("vip", (playersDB.has(p.uuid()) ? playersDB.getJSONObject(p.uuid()) : Util.addPlayerAccount(p.uuid(), null)).getJSONObject("vip"));

                if (req.queryParams("history") != null && req.queryParams("history").equals("true")) tmpPlayer.put("history", historyArray);


                playersArray.put(tmpPlayer);
            });
            */

            res.status(200);
            return playersObj.toString(4);
        } catch (Exception e) {
            if (e.getClass() == HaltException.class) throw (HaltException) e;
            res.status(500);
            return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
        }
    }
}

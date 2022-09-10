package MindustryBR.internal.API.Routes;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.Classes.API.BaseRoute;
import mindustry.gen.Groups;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.HaltException;
import spark.Request;
import spark.Response;

import static MindustryBR.Discord.Commands.GameInfo.stats;
import static MindustryBR.Mindustry.Events.worldLoad.started;
import static mindustry.Vars.state;

public class root extends BaseRoute {
    public static String get(Request req, Response res) {
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
            if (e.getClass() == HaltException.class) throw (HaltException) e;
            res.status(500);
            return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
        }
    }
}

package MindustryBR.internal.API.Routes.Player;

import MindustryBR.internal.Util;
import MindustryBR.internal.Classes.API.BaseRoute;
import mindustry.net.Administration;
import org.json.JSONObject;
import spark.HaltException;
import spark.Request;
import spark.Response;

import static MindustryBR.Main.knownIPtoCountry;
import static MindustryBR.Main.playersDB;
import static mindustry.Vars.netServer;

public class player extends BaseRoute {
    public static String get(Request req, Response res) {
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
                    .put("country", knownIPtoCountry.get(playerInfo.lastIP))
                    .put("vip", (playersDB.has(playerInfo.id) ? playersDB.getJSONObject(playerInfo.id) : Util.addPlayerAccount(playerInfo.id, null)).getJSONObject("vip"));

            res.status(200);
            return resObj.toString(4);
        } catch (Exception e) {
            if (e.getClass() == HaltException.class) throw (HaltException) e;
            res.status(500);
            return "{\"error\": \"" + (e.getMessage() == null || e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
        }
    }
}

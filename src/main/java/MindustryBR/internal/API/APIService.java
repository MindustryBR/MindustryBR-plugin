package MindustryBR.internal.API;

import MindustryBR.internal.API.Routes.Player.history;
import MindustryBR.internal.API.Routes.Player.player;
import MindustryBR.internal.API.Routes.players;
import MindustryBR.internal.API.Routes.root;
import arc.util.Log;
import org.json.JSONObject;

import static MindustryBR.Main.config;
import static spark.Spark.*;

public class APIService {
    public static void startWebServer() {
        if (!config.has("API") || config.getJSONObject("API").isEmpty() || !config.getJSONObject("API").has("port") || config.getJSONObject("API").getInt("port") == -1) {
            Log.info("[MindustryBR] API: Porta não definida");
            return;
        }

        int port = config.getJSONObject("API").getInt("port") > 0 ? config.getJSONObject("API").getInt("port") : 8080;

        port(port);

        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return new JSONObject().put("error", "Not found").toString(4);
        });

        internalServerError((req, res) -> {
            res.type("application/json");
            res.status(500);
            return new JSONObject().put("error", "Internal server error").toString(4);
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
            res.header("Access-Control-Allow-Credentials", "true");

            if(req.queryParams("auth") != null && req.queryParams("auth").equals(config.getJSONObject("API").getString("auth"))) {
                if (config.getJSONObject("API").getBoolean("log")) Log.info("[MindustryBR] API: Autorizado");
            } else {
                halt(401, "Unauthorized");
            }
        });


        get("/", root::get);
        get("/players", players::get);

        path("/player/:uuid", () -> {
            // GET /player/:uuid/
            get("/", player::get);

            get("/history", history::get);

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

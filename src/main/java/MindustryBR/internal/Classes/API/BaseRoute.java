package MindustryBR.internal.Classes.API;

import spark.Request;
import spark.Response;

public class BaseRoute {
    public static String get(Request req, Response res) {
        res.status(404);
        return null;
    }

    public static String post(Request req, Response res) {
        res.status(404);
        return null;
    }

    public static String put(Request req, Response res) {
        res.status(404);
        return null;
    }

    public static String delete(Request req, Response res) {
        res.status(404);
        return null;
    }
}

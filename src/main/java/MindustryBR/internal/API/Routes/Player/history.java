package MindustryBR.internal.API.Routes.Player;

import MindustryBR.internal.Classes.API.BaseRoute;
import MindustryBR.internal.Classes.History.LimitedQueue;
import MindustryBR.internal.Classes.History.entry.BaseEntry;
import arc.util.Strings;
import org.json.JSONArray;
import spark.HaltException;
import spark.Request;
import spark.Response;

import static MindustryBR.Main.playerHistory;

public class history extends BaseRoute {
    public static String get(Request req, Response res) {
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
            if (e.getClass() == HaltException.class) throw (HaltException) e;
            res.status(500);
            return "{\"error\": \"" + (e.getMessage().isBlank() ? "Unknown" : e.getMessage()) + "\"}";
        }
    }
}

package MindustryBR.Mindustry.Commands.server;

import arc.util.Log;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.logHistory;

public class historyLog {
    public static void run (DiscordApi bot, JSONObject config, String[] args) {
        logHistory = !logHistory;
        if (logHistory) {
            Log.info("[MindustryBR] History will now be logged in console");
        } else {
            Log.info("[MindustryBR] History will not be logged in console");
        }
    }
}

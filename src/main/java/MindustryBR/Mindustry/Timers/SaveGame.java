package MindustryBR.Mindustry.Timers;

import MindustryBR.internal.Util;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class SaveGame {
    public static void run(DiscordApi bot, JSONObject config) {
        Util.saveGame();
    }
}

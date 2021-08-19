package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import arc.util.Log;
import mindustry.game.EventType.WaveEvent;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.state;

public class wave {
    public static void run (DiscordApi bot, JSONObject config, WaveEvent e) {
        Util.saveGame();
        //Log.info(state.map.tags);
    }
}

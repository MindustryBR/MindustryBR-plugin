package MindustryBR.Events;

import arc.util.Log;
import mindustry.game.EventType.WaveEvent;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.*;

public class wave {
    public static void run (DiscordApi bot, JSONObject config, WaveEvent e) {
        Log.info("WaveEvent");
        Log.info(state.enemies);
        Log.info(state.wavetime);
    }
}

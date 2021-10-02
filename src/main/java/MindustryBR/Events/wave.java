package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import mindustry.game.EventType.WaveEvent;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.state;

public class wave {
    public static void run (DiscordApi bot, JSONObject config, WaveEvent e) {
        if (Groups.player.size() < 1) state.serverPaused = true;

        Util.saveGame();
    }
}

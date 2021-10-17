package MindustryBR.Mindustry.Events;

import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;

public class unitDestroy {
    public static void run (DiscordApi bot, JSONObject config, EventType.UnitDestroyEvent e) {
        stats.unitsDestroyed++;
    }
}

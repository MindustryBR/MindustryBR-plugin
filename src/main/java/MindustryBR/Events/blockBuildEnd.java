package MindustryBR.Events;

import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;

public class blockBuildEnd {
    public static void run (DiscordApi bot, JSONObject config, EventType.BlockBuildEndEvent e) {
        if (e.breaking) {
            stats.buildingsDesconstructed++;
        } else {
            stats.buildingsConstructed++;
        }
    }
}

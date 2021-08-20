package MindustryBR.Events;

import arc.util.Log;
import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;

public class unitDestroy {
    public static void run (DiscordApi bot, JSONObject config, EventType.UnitDestroyEvent e) {
        stats.unitsDestroyed++;

        //Log.info("Unit destroyed of team: " + e.unit.team.name);
    }
}

package MindustryBR.Events;

import arc.util.Log;
import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;

public class unitDrown {
    public static void run (DiscordApi bot, JSONObject config, EventType.UnitDrownEvent e) {
        stats.unitsDestroyed++;

        //Log.info("Unit drowned of team: " + e.unit.team.name);
    }
}

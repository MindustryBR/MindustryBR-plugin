package MindustryBR.Events;

import arc.util.Log;
import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;

public class unitCreate {
    public static void run (DiscordApi bot, JSONObject config, EventType.UnitCreateEvent e) {
        stats.unitsBuilt++;

        //Log.info("Unit created of team: " + e.unit.team.name);
    }
}

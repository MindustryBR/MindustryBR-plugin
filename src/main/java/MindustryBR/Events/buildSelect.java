package MindustryBR.Events;

import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import MindustryBR.internal.util.sendMsgToGame;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;


public class buildSelect {
    public static void run(DiscordApi bot, JSONObject config, EventType.BuildSelectEvent e) {
        if(!e.breaking && e.builder != null && e.builder.buildPlan() != null && e.builder.buildPlan().block == Blocks.thoriumReactor && e.builder.isPlayer()){
            //player is the unit controller
            Player player = e.builder.getPlayer();

            //send a message to everyone saying that this player has begun building a reactor
            String msg = "[scarlet]ALERTA[]: " + player.name + " está construindo um reator de torio em " + e.tile.x + ", " + e.tile.y;
            new sendMsgToDiscord(bot, config, msg);
            new sendLogMsgToDiscord(bot, config, msg);
            new sendMsgToGame(bot, "[red][Server][]", msg, config);
        }
    }
}

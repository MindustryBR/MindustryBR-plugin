package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import arc.util.Log;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.state;

public class playerLeave {
    public static void run(DiscordApi bot, JSONObject config, PlayerLeave e) {
        // Pause the game if no one is connected
        if (Groups.player.size()-1 < 1) {
            state.serverPaused = true;
            Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
            Call.sendMessage("[scarlet][Server][]: Jogo pausado...");

            new sendMsgToDiscord(bot, config, "**[Server]:** Jogo pausado...");
            new sendLogMsgToDiscord(bot, config, "**[Server]:** Jogo pausado...");
        }

        // Send disconnect message to discord
        String msg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** desconectou";
        new sendMsgToDiscord(bot, config, msg);

        String logMsg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** (" + e.player.getInfo().id + ") desconectou";
        new sendLogMsgToDiscord(bot, config, logMsg);
    }
}

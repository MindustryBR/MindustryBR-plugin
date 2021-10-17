package MindustryBR.Mindustry.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.JoinLeaveEntry;
import MindustryBR.internal.Util;
import MindustryBR.internal.dcRelay.sendLogMsgToDiscord;
import MindustryBR.internal.dcRelay.sendMsgToDiscord;
import arc.util.Log;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.playerHistory;
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

        if (playerHistory.get(e.player.getInfo().id) == null) playerHistory.put(e.player.getInfo().id, new LimitedQueue<>(20));

        LimitedQueue<BaseEntry> history = playerHistory.get(e.player.getInfo().id);
        JoinLeaveEntry historyEntry = new JoinLeaveEntry(e.player, false);
        history.add(historyEntry);
        playerHistory.put(e.player.getInfo().id, history);

        // Send disconnect message to discord
        String msg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** desconectou";
        new sendMsgToDiscord(bot, config, msg);

        String logMsg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** (" + e.player.getInfo().id + ") desconectou";
        new sendLogMsgToDiscord(bot, config, logMsg);
    }
}

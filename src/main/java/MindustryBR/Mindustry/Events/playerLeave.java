package MindustryBR.Mindustry.Events;

import MindustryBR.internal.DiscordRelay;
import MindustryBR.internal.Util;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.JoinLeaveEntry;
import arc.util.Log;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.playerHistory;
import static MindustryBR.Mindustry.Commands.client.RTV.ratio;
import static MindustryBR.Mindustry.Commands.client.RTV.votes;
import static mindustry.Vars.state;

public class playerLeave {
    public static void run(DiscordApi bot, JSONObject config, PlayerLeave e) {
        // Pause the game if no one is connected
        if (Groups.player.size() - 1 < 1) {
            state.serverPaused = true;
            Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
            Call.sendMessage("[scarlet][Server][]: Jogo pausado...");

            DiscordRelay.sendMsgToDiscord("**[Server]:** Jogo pausado...");
            DiscordRelay.sendLogMsgToDiscord("**[Server]:** Jogo pausado...");
        }

        if (playerHistory.get(e.player.getInfo().id) == null)
            playerHistory.put(e.player.getInfo().id, new LimitedQueue<>(20));

        LimitedQueue<BaseEntry> history = playerHistory.get(e.player.getInfo().id);
        JoinLeaveEntry historyEntry = new JoinLeaveEntry(e.player, false);
        history.add(historyEntry);
        playerHistory.put(e.player.getInfo().id, history);

        // Send disconnect message to discord
        String msg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** desconectou";
        DiscordRelay.sendMsgToDiscord(msg);

        String logMsg = ":outbox_tray: **" + Util.handleName(e.player, true) + "** (" + e.player.getInfo().id + ") desconectou";
        DiscordRelay.sendLogMsgToDiscord(logMsg);


        Player player = e.player;
        int cur = votes.size();
        int req = (int) Math.ceil(ratio * Groups.player.size());
        if(votes.contains(player.uuid())) {
            votes.remove(player.uuid());
            Call.sendMessage("RTV: [accent]" + player.name + "[] saiu, [green]" + cur + "[] votos, [green]" + req + "[] necessarios");
        }
    }
}

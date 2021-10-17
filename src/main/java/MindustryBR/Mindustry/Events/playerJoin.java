package MindustryBR.Mindustry.Events;

import MindustryBR.internal.Util;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.JoinLeaveEntry;
import MindustryBR.internal.dcRelay.sendLogMsgToDiscord;
import MindustryBR.internal.dcRelay.sendMsgToDiscord;
import arc.util.Log;
import mindustry.game.EventType.PlayerJoin;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.playerHistory;
import static mindustry.Vars.state;

public class playerJoin {
    public static void run(DiscordApi bot, JSONObject config, PlayerJoin e) {
        // Check for non-admin players with admin in name
        e.player.name = Util.handleName(e.player, false);

        if (playerHistory.get(e.player.getInfo().id) == null) playerHistory.put(e.player.getInfo().id, new LimitedQueue<>(20));

        LimitedQueue<BaseEntry> history = playerHistory.get(e.player.getInfo().id);
        JoinLeaveEntry historyEntry = new JoinLeaveEntry(e.player);
        history.add(historyEntry);
        playerHistory.put(e.player.getInfo().id, history);

        // Rename players to use the tag system
        JSONObject prefix = config.getJSONObject("prefix");

        if (e.player.getInfo().id.equals(config.getString("owner_id"))) {
            e.player.name = prefix.getString("owner_prefix").replace("%1", e.player.name);
        } else if (e.player.admin) {
            e.player.name = prefix.getString("admin_prefix").replace("%1", e.player.name);
        } else {
            e.player.name = prefix.getString("user_prefix").replace("%1", e.player.name);
        }

        // Unpause the game if one or more player is connected
        if (Groups.player.size() >= 1 && state.serverPaused) {
            state.serverPaused = false;
            Log.info("auto-pause: " + Groups.player.size() + " jogador conectado -> Jogo despausado...");
            Call.sendMessage("[scarlet][Server][]: Jogo despausado...");

            new sendMsgToDiscord(bot, config, "**[Server]:** Jogo despausado...");
            new sendLogMsgToDiscord(bot, config, "**[Server]:** Jogo despausado...");
        }

        // Send connect message to discord
        String msg = ":inbox_tray: **" + Util.handleName(e.player, true) + "** conectou";
        new sendMsgToDiscord(bot, config, msg);

        String logMsg = ":inbox_tray: **" + Util.handleName(e.player, true) + "** (" + e.player.getInfo().id + ") conectou";
        new sendLogMsgToDiscord(bot, config, logMsg);
    }
}

package MindustryBR.Mindustry.Events;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.DiscordRelay;
import MindustryBR.internal.Util;
import MindustryBR.internal.Classes.History.LimitedQueue;
import MindustryBR.internal.Classes.History.entry.BaseEntry;
import MindustryBR.internal.Classes.History.entry.JoinLeaveEntry;
import arc.util.Log;
import mindustry.game.EventType.PlayerJoin;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.IOException;

import static MindustryBR.Main.*;
import static mindustry.Vars.state;

public class playerJoin {
    public static void run(DiscordApi bot, JSONObject config, PlayerJoin e) throws IOException {
        // Check for non-admin players with admin in name
        e.player.name = Util.handleName(e.player, false);

        if (playerHistory.get(e.player.uuid()) == null)
            playerHistory.put(e.player.uuid(), new LimitedQueue<>(20));

        if (!playersDB.has(e.player.uuid()))
            Util.addPlayerAccount(e.player.uuid(), null);

        LimitedQueue<BaseEntry> history = playerHistory.get(e.player.getInfo().id);
        JoinLeaveEntry historyEntry = new JoinLeaveEntry(e.player);
        history.add(historyEntry);
        playerHistory.put(e.player.getInfo().id, history);

        // Rename players to use the tag system
        JSONObject prefix = config.getJSONObject("prefix");

        if (config.getJSONArray("owner_id").toList().contains(e.player.uuid())) {
            e.player.name = prefix.getString("owner_prefix").replace("%1", e.player.name);
        } else if (e.player.admin) {
            e.player.name = prefix.getString("admin_prefix").replace("%1", e.player.name);
        } else {
            e.player.name = prefix.getString("user_prefix").replace("%1", e.player.name);
        }

        if (Util.isVIP(e.player.uuid())) e.player.name = "[#008717][VIP][] " + e.player.name;

        // Unpause the game if one or more player is connected
        if (Groups.player.size() >= 1 && state.serverPaused) {
            state.serverPaused = false;
            Log.info("auto-pause: " + Groups.player.size() + " jogador conectado -> Jogo despausado...");
            Call.sendMessage("[scarlet][Server][]: Jogo despausado...");

            if (bot != null && Bot.logged) {
                DiscordRelay.sendMsgToDiscord("**[Server]:** Jogo despausado...");
                DiscordRelay.sendLogMsgToDiscord("**[Server]:** Jogo despausado...");
            }
        }


        if (bot == null || !Bot.logged) return;
        // Send connect message to discord
        String msg = ":inbox_tray: **" + Util.handleName(e.player, true) + "** conectou";
        DiscordRelay.sendMsgToDiscord(msg);

        String logMsg = ":inbox_tray: **" + Util.handleName(e.player, true) + "** (" + e.player.getInfo().id + ") conectou";
        DiscordRelay.sendLogMsgToDiscord(logMsg);
    }
}

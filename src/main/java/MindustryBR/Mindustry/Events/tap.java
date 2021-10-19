package MindustryBR.Mindustry.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.activeHistoryPlayers;
import static MindustryBR.Main.worldHistory;

public class tap {
    public static void run(DiscordApi bot, JSONObject config, EventType.TapEvent e) {
        if (activeHistoryPlayers.contains(e.player)) {
            LimitedQueue<BaseEntry> tileHistory = worldHistory[e.tile.x][e.tile.y];

            StringBuilder message = new StringBuilder("[yellow]Historico do bloco (" + e.tile.x + "," + e.tile.y + ")");

            if (tileHistory.isOverflown()) message.append("\n[white]... historico muito grande");
            for (BaseEntry historyEntry : tileHistory) message.append("\n").append(historyEntry.getMessage());
            if (tileHistory.isEmpty()) message.append("\n[royal]* [white]sem historico");

            e.player.sendMessage(message.toString());
        }
    }
}

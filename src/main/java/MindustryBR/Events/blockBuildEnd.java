package MindustryBR.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.BlockEntry;
import MindustryBR.internal.classes.history.entry.JoinLeaveEntry;
import arc.struct.Seq;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.world.Tile;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;
import static MindustryBR.Main.playerHistory;
import static MindustryBR.Main.worldHistory;

public class blockBuildEnd {
    public static void run (DiscordApi bot, JSONObject config, EventType.BlockBuildEndEvent e) {
        if (e.breaking) {
            stats.buildingsDesconstructed++;
        } else {
            stats.buildingsConstructed++;
        }

        BaseEntry historyEntry = new BlockEntry(e);

        Player player = e.unit.getPlayer();

        if (player == null) {
            //System.out.println("Player null");
            return;
        }

        if (playerHistory.get(player.getInfo().id) == null) playerHistory.put(player.getInfo().id, new LimitedQueue<>(20));

        LimitedQueue<BaseEntry> history = playerHistory.get(player.getInfo().id);
        history.add(historyEntry);
        playerHistory.put(player.getInfo().id, history);

        Seq<Tile> linkedTile = e.tile.getLinkedTiles(new Seq<>());
        for (Tile tile : linkedTile) {
            worldHistory[tile.x][tile.y].add(historyEntry);
        }
    }
}

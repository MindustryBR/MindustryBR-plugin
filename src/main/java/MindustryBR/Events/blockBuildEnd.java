package MindustryBR.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.BlockEntry;
import MindustryBR.internal.classes.history.entry.JoinLeaveEntry;
import arc.struct.Seq;
import mindustry.game.EventType;
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

        LimitedQueue<BaseEntry> history = playerHistory.get(e.unit.getPlayer().getInfo().id);
        history.add(historyEntry);
        playerHistory.put(e.unit.getPlayer().getInfo().id, history);

        Seq<Tile> linkedTile = e.tile.getLinkedTiles(new Seq<>());
        for (Tile tile : linkedTile) {
            worldHistory[tile.x][tile.y].add(historyEntry);
        }
    }
}

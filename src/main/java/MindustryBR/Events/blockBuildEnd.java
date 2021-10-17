package MindustryBR.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.BlockEntry;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.world.Tile;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Discord.Commands.GameInfo.stats;
import static MindustryBR.Main.*;
import static MindustryBR.internal.util.Util.getLocalized;

public class blockBuildEnd {
    public static void run (DiscordApi bot, JSONObject config, EventType.BlockBuildEndEvent e) {
        if (e.breaking) {
            stats.buildingsDeconstructed++;
        } else stats.buildingsConstructed++;

        if (e.unit.getPlayer() == null) return;
        if (logHistory) Log.info((e.breaking ? "Block deconstructed" : "Block constructed") + " by " + e.unit.getPlayer().name() + " (" + e.tile.x + "," + e.tile.y + ") " + getLocalized(e.tile.block().name));

        Player player = e.unit.getPlayer();
        if (playerHistory.get(player.getInfo().id) == null) playerHistory.put(player.getInfo().id, new LimitedQueue<>(20));

        BaseEntry historyEntry = new BlockEntry(e);
        LimitedQueue<BaseEntry> history = playerHistory.get(player.getInfo().id);
        history.add(historyEntry);
        playerHistory.put(player.getInfo().id, history);

        Seq<Tile> linkedTile = e.tile.getLinkedTiles(new Seq<>());
        for (Tile tile : linkedTile) {
            worldHistory[tile.x][tile.y].add(historyEntry);
        }
    }
}

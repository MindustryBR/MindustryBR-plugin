package MindustryBR.Events;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.classes.history.entry.ConfigEntry;
import arc.struct.Seq;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Category;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.worldHistory;

public class configEvent {
    public static void run (DiscordApi bot, JSONObject config, EventType.ConfigEvent e) {
        if (e.player == null) return;
        if (e.tile.tile.x > worldHistory.length || e.tile.tile.y > worldHistory[0].length) return;
        //System.out.println(e.tile.tile.x + " " + e.tile.tile.y + " " + e.value + " " + e.value.getClass().getSimpleName());

        LimitedQueue<BaseEntry> tileHistory = worldHistory[e.tile.tile.x][e.tile.tile.y];
        boolean connect = true;

        if (!tileHistory.isEmpty() && tileHistory.getLast() instanceof ConfigEntry) {
            ConfigEntry lastConfigEntry = ((ConfigEntry) tileHistory.getLast());

            connect = !(lastConfigEntry.value == e.value && lastConfigEntry.connect);
        }

        BaseEntry historyEntry = new ConfigEntry(e, connect);

        Seq<Building> linkedTile = e.tile.getPowerConnections(new Seq<>());


        if (linkedTile.size <= 0 && e.tile.tile.block().category != Category.power) {
            worldHistory[e.tile.tile.x][e.tile.tile.y].add(historyEntry);
            return;
        }

        for (Building tile : linkedTile) {
            worldHistory[tile.tile.x][tile.tile.y].add(historyEntry);
        }
    }
}

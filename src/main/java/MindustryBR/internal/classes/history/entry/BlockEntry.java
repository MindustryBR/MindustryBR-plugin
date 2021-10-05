package MindustryBR.internal.classes.history.entry;


import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.world.Block;

import static MindustryBR.internal.util.Util.getLocalized;

public class BlockEntry implements BaseEntry {
    public Player player;
    public Block block;
    public boolean breaking;

    public BlockEntry(EventType.BlockBuildEndEvent e) {
        this.player = e.unit.getPlayer();
        this.block = e.tile.block();
        this.breaking = e.breaking;
    }

    @Override
    public String getMessage() {
        if (breaking) return "[red]- [white]" + player.name + " quebrou esse bloco";
        else return "[green]+ [white]" + player.name + " construiu " + (block != null ? "[purple]" + getLocalized(block.name) + "[white]": "esse bloco");
    }
}

package MindustryBR.internal.classes.history.entry;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.UnitCommand;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.world.Block;

import java.util.HashMap;

public class ConfigEntry implements BaseEntry{
    private static final HashMap<String, String> icons = new HashMap<>() {{
        put("copper", "\uF838");
        put("lead", "\uF837");
        put("metaglass", "\uF836");
        put("graphite", "\uF835");
        put("sand", "\uF834");
        put("coal", "\uF833");
        put("titanium", "\uF832");
        put("thorium", "\uF831");
        put("scrap", "\uF830");
        put("silicon", "\uF82F");
        put("plastanium", "\uF82E");
        put("phase-fabric", "\uF82D");
        put("surge-alloy", "\uF82C");
        put("spore-pod", "\uF82B");
        put("blast-compound", "\uF82A");
        put("pyratite", "\uF829");

        put("water", "\uF828");
        put("slag", "\uF827");
        put("oil", "\uF826");
        put("cryofluid", "\uF825");
    }};

    private static final String[] commands = {"[red]attack[white]", "[yellow]retreat[white]", "[orange]rally[white]"};


    public Player player;
    public Block block;
    public Object value;
    public boolean connect;

    public ConfigEntry(EventType.ConfigEvent e, boolean connect) {
        this.player = e.player;
        this.block = e.tile.block();
        this.value = e.value;
        this.connect = connect;
    }

    @Override
    public String getMessage() {
        return "";
    }
}

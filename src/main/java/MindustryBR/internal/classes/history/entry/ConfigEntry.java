package MindustryBR.internal.classes.history.entry;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import java.util.HashMap;

import static MindustryBR.internal.util.Util.getLocalized;

public class ConfigEntry implements BaseEntry{
    private static final HashMap<String, String[]> units = new HashMap<>() {{
        put("ground-factory", new String[] {
                "dagger",
                "crawler",
                "nova"
        });
        put("air-factory", new String[] {
                "flare",
                "mono"
        });
        put("naval-factory", new String[] {
                "risso"
        });
    }};

    public Player player;
    public Building building;
    public Object value;
    public boolean connect;
    public Tile target;

    public ConfigEntry(Player player, Building build, Object value, boolean connect, Tile target) {
        this.player = player;
        this.building = build;
        this.value = value;
        this.connect = connect;
        this.target = target;
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append("[orange]~ [white]").append(player.name).append("[white]");

        // Wtf im doing ;-;
        if (building.block() == Blocks.powerNode || building.block() == Blocks.powerNodeLarge || building.block() == Blocks.powerSource || building.block() == Blocks.powerVoid || building.block() == Blocks.surgeTower || building.block() == Blocks.phaseConduit || building.block() == Blocks.phaseConveyor || building.block() == Blocks.bridgeConduit || building.block() == Blocks.itemBridge || building.block() == Blocks.massDriver) {
            if (connect) {
                if ((int) value == -1) {
                    msg.append(" [red]desconectou[white] esse bloco");
                    return msg.toString();
                }

                msg.append(" [green]conectou[white] ").append("a ").append(getLocalized(target.block().name)).append(" (").append(target.x).append(",").append(target.y).append(")");
            } else {
                msg.append(" [red]desconectou[white] ").append("de ").append(getLocalized(target.block().name)).append(" (").append(target.x).append(",").append(target.y).append(")");
            }
        } else if (building.block() == Blocks.groundFactory || building.block() == Blocks.airFactory || building.block() == Blocks.navalFactory) {
            if ((int) value == -1) {
                msg.append(" [red]desativou[white] [purple]").append(getLocalized(building.block().name)).append("[white]");
                return msg.toString();
            }
            msg.append(" mandou ").append(getLocalized(building.block().name)).append(" fabricar ").append(getLocalized(units.get(building.block().name)[(int) value]));
        } else if (building.block() == Blocks.door || building.block() == Blocks.doorLarge) {
            if (!((boolean) value)) {
                msg.append(" fechou a porta");
                return msg.toString();
            }
            msg.append(" abriu a porta");
        } else if (building.block() == Blocks.commandCenter) {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }
            msg.append(" comandou as unidades para ").append(getLocalized(value.toString()));
        } else if (building.block() == Blocks.liquidSource) {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }
            msg.append(" mudou o liquido para ");
            if (Vars.content.liquids().contains((Liquid) value)) {
                Liquid liquid = Vars.content.liquids().find(i -> i.equals(value));
                msg.append(getLocalized(liquid.name));
            } else {
                msg.append("desconhecido");
            }
        } else {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }
            msg.append(" mudou o item para ");
            if (Vars.content.items().contains((Item) value)) {
                Item item = Vars.content.items().find(i -> i.equals(value));
                msg.append(getLocalized(item.name));
            } else {
                msg.append("desconhecido");
            }
        }

        return msg.toString();
    }
}

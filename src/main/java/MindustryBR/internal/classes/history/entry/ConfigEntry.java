package MindustryBR.internal.classes.history.entry;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.nio.charset.StandardCharsets;
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

        if (building == null) return "";

        // Wtf im doing ;-;
        if (building.block() == Blocks.powerNode || building.block() == Blocks.powerNodeLarge || building.block() == Blocks.powerSource || building.block() == Blocks.powerVoid || building.block() == Blocks.surgeTower || building.block() == Blocks.phaseConduit || building.block() == Blocks.phaseConveyor || building.block() == Blocks.bridgeConduit || building.block() == Blocks.itemBridge || building.block() == Blocks.massDriver) {
            if (connect) {
                getConnect(msg);
            } else {
                msg.append(" [red]desconectou[white] ").append("de ").append(getLocalized(target.block().name)).append(" (").append(target.x).append(",").append(target.y).append(")");
            }
            return msg.toString();
        } else if (building.block() == Blocks.groundFactory || building.block() == Blocks.airFactory || building.block() == Blocks.navalFactory) {
            if ((int) value == -1) {
                msg.append(" [red]desativou[white] [purple]").append(getLocalized(building.block().name)).append("[white]");
            } else if (units.get(building.block().name).length > (int) value) {
                msg.append(" mandou ").append(getLocalized(building.block().name)).append(" fabricar ").append(getLocalized(units.get(building.block().name)[(int) value]));
            } else {
                getConnect(msg);
            }
            return msg.toString();
        } else if (building.block() == Blocks.door || building.block() == Blocks.doorLarge) {
            if (!((boolean) value)) {
                msg.append(" fechou a porta");
                return msg.toString();
            }
            msg.append(" abriu a porta");
            return msg.toString();
        } else if (building.block() == Blocks.commandCenter) {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }
            msg.append(" comandou as unidades para ").append(getLocalized(value.toString()));
            return msg.toString();
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
            return msg.toString();
        } else if (building.block() == Blocks.sorter || building.block() == Blocks.itemSource || building.block() == Blocks.invertedSorter || building.block() == Blocks.unloader) {
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
            return msg.toString();
        } else if (building.block().category.equals(Category.logic)) {
            if (value.getClass().getSimpleName().equals("byte[]")) {
                msg.append(" mudou o codigo do ").append(getLocalized(building.block().name));
                return msg.toString();
            } else if (building.block() == Blocks.message) {
                String str = (String) value;
                String substr = str.length() > 20 ? "\"" + str.substring(0, 10) + "\"..." : str;
                msg.append(" mudou a mensagem para ").append(substr);
                return msg.toString();
            } else if (value.getClass().getSimpleName().equals("Integer")) {
                getConnect(msg);
                return msg.toString();
            }
            msg.append(" mudou a configuracao do ").append(getLocalized(building.block().name)).append(" para ").append(value);
        } else {
            if (value.getClass().getSimpleName().equals("byte[]")) {
                String strValue = new String((byte[]) value, StandardCharsets.UTF_8);
                msg.append(" mudou as configuracoes para ").append(strValue);
            } else {
                msg.append(" mudou as configuracoes para ").append(value);
            }
        }

        return msg.toString();
    }

    public void getConnect(StringBuilder msg) {
        if (value.getClass().getSimpleName().equals("Integer") && (int) value == -1) {
            msg.append(" [red]desconectou[white] esse bloco");
            return;
        }

        msg.append(" [green]conectou[white] ").append("a ").append(getLocalized(target.block().name)).append(" (").append(target.x).append(",").append(target.y).append(")");
    }
}

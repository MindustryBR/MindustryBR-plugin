package MindustryBR.internal.classes.history.entry;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.HashMap;

import static MindustryBR.Main.resources;

public class ConfigEntry implements BaseEntry{
    private static final HashMap<String, String[]> units = new HashMap<>() {{
        put("ground-factory", new String[] {
                "[orange]Adaga[white]",
                "[purple]Rastejante[white]",
                "[green]Nova[white]"
        });
        put("air-factory", new String[] {
                "[orange]Flare[white]",
                "[green]Mono[white]"
        });
        put("naval-factory", new String[] {
                "[orange]Risso[white]"
        });
    }};
    private static final HashMap<String, String> commands = new HashMap<>() {{
        put("attack", "[red]atacar[white]");
        put("rally", "[orange]reunir[white]");
        put("idle", "[yellow]parar[white]");
    }};

    public Player player;
    public Block block;
    public Building building;
    public Tile tile;
    public Object value;
    public boolean connect;

    public ConfigEntry(EventType.ConfigEvent e, boolean connect) {
        this.player = e.player;
        this.block = e.tile.block();
        this.building = e.tile;
        this.tile = e.tile.tile;
        this.value = e.value;
        this.connect = connect;
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append("[orange]~ [white]").append(player.name).append("[white]");

        // Wtf im doing ;-;
        if (block == Blocks.powerNode || block == Blocks.powerNodeLarge || block == Blocks.powerSource || block == Blocks.powerVoid || block == Blocks.surgeTower || block == Blocks.phaseConduit || block == Blocks.phaseConveyor || block == Blocks.bridgeConduit || block == Blocks.itemBridge || block == Blocks.massDriver) {
            if (connect) {
                if ((int) value == -1) {
                    msg.append(" [red]desconnectou[white] ").append(block.emoji()).append(" ").append(block.localizedName);
                    return msg.toString();
                }

                msg.append(" [green]connectou[white] ");
                getBlockInPos(msg);
                msg.append(" a ").append(block.localizedName).append(" (").append(tile.x).append(",").append(tile.y).append(")");
            } else {
                msg.append(" [red]desconnectou[white] ");
                getBlockInPos(msg);
                msg.append(" de ").append(block.localizedName).append(" (").append(tile.x).append(",").append(tile.y).append(")");
            }
        } else if (block == Blocks.groundFactory || block == Blocks.airFactory || block == Blocks.navalFactory) {
            if ((int) value == -1) {
                msg.append(" desativou a fabrica");
                return msg.toString();
            }
            msg.append(" mandou fabricar ").append(units.get(block.name)[(int) value]);
        } else if (block == Blocks.commandCenter) {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }
            msg.append(" comandou as unidades para ").append(commands.get(value.toString()));
        } else if (block == Blocks.liquidSource) {
            if (value == null) {
                msg.append(" mudou as configuracoes para o padrao");
                return msg.toString();
            }

            msg.append(" mudou o liquido para ");

            if (Vars.content.liquids().contains((Liquid) value)) {
                Liquid liquid = Vars.content.liquids().find(i -> i.equals(value));
                msg.append(resources.get(liquid.name) != null ? resources.get(liquid.name) : liquid.localizedName);
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
                msg.append(resources.get(item.name) != null ? resources.get(item.name) : item.localizedName);
            } else {
                msg.append("desconhecido");
            }
        }

        return msg.toString();
    }

    public void getBlockInPos(StringBuilder msg) {
        if ((int) value == -1) {
            msg.append("desconhecido");
            return;
        }

        if (Vars.world.tile((int) value) != null) {
            Tile t = Vars.world.tile((int) value);
            if (t.block() != null) {
                msg.append(" ").append(t.block().localizedName);
            } else {
                msg.append("desconhecido");
            }
        } else {
            msg.append("desconhecido");
        }
    }
}

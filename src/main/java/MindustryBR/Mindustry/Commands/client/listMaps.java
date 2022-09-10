package MindustryBR.Mindustry.Commands.client;

import MindustryBR.internal.Classes.Commands.ClientCommand;
import arc.util.Nullable;
import mindustry.gen.Player;

import static mindustry.Vars.maps;

public class listMaps implements ClientCommand {
    @Nullable
    public static final String params = "";
    @Nullable
    public static final String desc = "Lista os mapas.";

    public static void run(String[] args, Player player) {
        if (maps.customMaps().size == 0) {
            player.sendMessage("[scarlet]Nenhum mapa encontrado!");
            return;
        }

        player.sendMessage("[lightgray]Mapas:");
        maps.customMaps().forEach(map -> {
            player.sendMessage("[white]" + map.name() + " - " + map.file.name());
        });
    }
}

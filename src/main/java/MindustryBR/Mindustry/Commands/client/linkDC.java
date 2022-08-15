package MindustryBR.Mindustry.Commands.client;

import MindustryBR.internal.Util;
import MindustryBR.internal.classes.commands.ClientCommand;
import arc.util.Nullable;
import mindustry.gen.Player;

import java.util.Iterator;

import static MindustryBR.Main.linkCodes;
import static MindustryBR.Main.playersDB;

public class linkDC implements ClientCommand {
    public static final boolean adminOnly = false;
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = "Link sua conta do Mindusty com o iscord";

    public static void run(String[] args, Player player) {
        for (String id : linkCodes.values()) {
            if (id.equals(player.getInfo().id)) {
                for (String key : linkCodes.keys()) {
                    if (linkCodes.get(key).equals(id)) {
                        player.sendMessage("Seu codigo: " + key);
                        return;
                    }
                }
                return;
            }
        }

        for (Iterator<String> it = playersDB.keys(); it.hasNext(); ) {
            String k = it.next();
            for (int i = 0; i < playersDB.getJSONObject(k).getJSONArray("accounts").length(); i++) {
                if (playersDB.getJSONObject(k).getJSONArray("accounts").get(i).equals(player.uuid())) {
                    player.sendMessage("Voce ja linkou sua conta");
                    return;
                }
            }
        }

        String code = Util.randomCode(8);
        linkCodes.put(code, player.uuid());
        player.sendMessage("Seu codigo: " + code + "\nUse o comando !link <codigo> na DM do ReactorBot no nosso servidor do [blue]discord[]: [sky]https://discord.gg/Rt5HjqW[] para linkar sua conta");
    }
}
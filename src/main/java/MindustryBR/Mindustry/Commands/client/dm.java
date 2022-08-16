package MindustryBR.Mindustry.Commands.client;

import MindustryBR.internal.classes.commands.ClientCommand;
import arc.util.Nullable;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class dm implements ClientCommand {
    @Nullable
    public static final String params = "<player> <message...>";
    @Nullable
    public static final String desc = "Mande uma mensagem privada para um jogador.";

    public static void run(String[] args, Player player) {
        // Find player by name
        Player other = Groups.player.find(p -> p.name.toLowerCase().contains(args[0].toLowerCase()));

        // Give error message if player isn't found
        if (other == null) {
            player.sendMessage("[scarlet]Nenhum jogador encontrado com esse nome!");
            return;
        }

        // Send the other player a private message
        other.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
        // Send a message to the player that used the command
        player.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
    }
}

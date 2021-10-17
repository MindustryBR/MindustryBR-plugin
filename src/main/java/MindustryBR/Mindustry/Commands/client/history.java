package MindustryBR.Mindustry.Commands.client;

import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static MindustryBR.Main.activeHistoryPlayers;

public class history {
    public static void run (DiscordApi bot, JSONObject config, String[] args, Player player) {
        if (activeHistoryPlayers.contains(player)) {
            activeHistoryPlayers.remove(player);
            player.sendMessage("[yellow]Historico [red]desativado[yellow].");
        } else {
            activeHistoryPlayers.add(player);
            player.sendMessage("[yellow]Historico [green]ativado[yellow]. Clique em um bloco para ver o historico");
        }
    }
}

package MindustryBR.Mindustry.Commands.client;

import MindustryBR.internal.Classes.Commands.ClientCommand;
import arc.Events;
import arc.util.Nullable;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.maps.Map;

import java.util.HashSet;

/**
 * Adapted from <a href="https://github.com/mayli/RockTheVotePlugin">mayli/RockTheVotePlugin</a>
 */
public class RTV implements ClientCommand {
    @Nullable
    public static final String params = "[map]";
    @Nullable
    public static final String desc = "Pula o mapa atual.";

    public static final double ratio = 0.6;
    public static HashSet<String> votes = new HashSet<>();
    public static boolean enable = true;

    @Nullable
    public static Map nextMap = null;

    public static void run(String[] args, Player player) {
        if (player.admin()){
            enable = args.length != 1 || !args[0].equals("off");
        }

        if (!enable) {
            player.sendMessage("RTV: RockTheVote esta desativado");
            return;
        }

        votes.add(player.uuid());
        int cur = votes.size();
        int req = (int) Math.ceil(ratio * Groups.player.size());
        Call.sendMessage("RTV: [accent]" + player.name + "[] quer mudar de mapa, [green]" + cur +
                "[] votos, [green]" + req + "[] necessários.");

        if (cur < req) {
            return;
        }

        votes.clear();
        Call.sendMessage("RTV: [green] votação concluida, mudando mapa.");
        Events.fire(new EventType.GameOverEvent(Team.crux));
    }
}

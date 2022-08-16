package MindustryBR.Mindustry.Commands.server;

import MindustryBR.internal.classes.commands.ServerCommand;
import arc.Events;
import arc.util.Log;
import arc.util.Nullable;
import mindustry.game.EventType;
import mindustry.game.Team;

import static mindustry.Vars.state;
import static MindustryBR.Mindustry.Events.gameover.inExtraRound;

public class gameoverServer implements ServerCommand {
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = "Finaliza o jogo.";

    public static void run(String[] args) {
        if(state.isMenu()){
            Log.err("Not playing a map.");
            return;
        }

        Log.info("Core destroyed.");
        inExtraRound = false;
        Events.fire(new EventType.GameOverEvent(Team.crux));
    }
}

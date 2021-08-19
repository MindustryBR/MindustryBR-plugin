package MindustryBR.internal.util;

import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Groups;

import java.util.TimerTask;

import static mindustry.Vars.state;

public class PauseTask extends TimerTask {
    @Override
    public void run() {
        if (state.serverPaused) return;

        // Pause the game if no one is connected
        if (Groups.player.size()-1 < 1) {
            state.serverPaused = true;
            Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
            Call.sendMessage("[scarlet][Server][]: Jogo pausado...");
        }
    }
}

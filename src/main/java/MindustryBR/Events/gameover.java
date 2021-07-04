package MindustryBR.Events;

import arc.util.Log;
import mindustry.game.EventType.GameOverEvent;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.state;

public class gameover {
    public static void run(DiscordApi bot, JSONObject config, GameOverEvent e) {
        Log.info("tempo: " + state.stats.timeLasted);
        Log.info("blocos construidos: " + state.stats.buildingsBuilt);
        Log.info("blocos descontruidos: " + state.stats.buildingsDeconstructed);
        Log.info("blocos destruidos: " + state.stats.buildingsDestroyed);
        Log.info("unidades inimigas destruidas: " + state.stats.enemyUnitsDestroyed);
    }
}

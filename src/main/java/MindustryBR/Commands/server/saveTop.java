package MindustryBR.Commands.server;

import arc.Core;
import arc.files.Fi;
import arc.util.Log;
import mindustry.core.GameState.State;
import mindustry.io.SaveIO;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.time.LocalDateTime;

import static mindustry.Vars.*;

public class saveTop {
    public static void run (DiscordApi bot, JSONObject config, String[] args) {
        if(state.is(State.playing)){
            Log.info("&lm[autosave]");
            Fi file = saveDirectory.child(LocalDateTime.now().toString().substring(0, 19).replace("T", "-").replace(":", "_") + "--" + state.map.name() + "." + saveExtension);

            Core.app.post(() -> SaveIO.save(file));
        }
    }
}

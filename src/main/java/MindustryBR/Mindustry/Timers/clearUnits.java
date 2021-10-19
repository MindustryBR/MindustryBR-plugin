package MindustryBR.Mindustry.Timers;

import MindustryBR.internal.Util;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import static mindustry.Vars.netServer;

public class clearUnits {
    public static void run(DiscordApi bot, JSONObject config) {
        JSONObject options = config.getJSONObject("options");
        boolean clear = options.getBoolean("clearUnits");

        if (Vars.state.isPlaying() && Vars.net.server() && Groups.unit.size() > options.getInt("clearUnitsAmount") && !Vars.state.serverPaused && clear) {
            Call.sendMessage("Mais de " + options.getInt("clearUnitsAmount") + " unidades detectadas. Limpando...");
            Groups.unit.each(Unitc::kill);
            Call.sendMessage("Sincronizando jogadores");

            Groups.player.each(player -> {
                if (!player.dead() && player.unit().isCommanding()) {
                    player.unit().clearCommand();
                }

                player.getInfo().lastSyncTime = Time.millis();
                Call.worldDataBegin(player.con);
                netServer.sendWorldData(player);
            });
        }

        Util.saveGame();
    }
}

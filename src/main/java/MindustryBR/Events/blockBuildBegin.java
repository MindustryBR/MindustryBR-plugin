package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import MindustryBR.internal.util.sendMsgToGame;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class blockBuildBegin {
    public static void run(DiscordApi bot, JSONObject config, EventType.BlockBuildBeginEvent e) {
        if(!e.breaking && e.unit.getPlayer() != null && e.unit.buildPlan() != null && e.unit.buildPlan().block == Blocks.thoriumReactor && e.unit.isPlayer()){
            Player player = e.unit.getPlayer();
            Seq<CoreBlock.CoreBuild> cores = player.team().cores();
            boolean temp = false;

            for (CoreBlock.CoreBuild core : cores) {
                double dis = Util.distanceBetweenPoints(e.tile.x, e.tile.y, core.tile.x, core.tile.y);
                System.out.println("tile: " + e.tile.x + " " + e.tile.y + "  core: " + core.x + " " + core.y);
                System.out.println(dis);
                if (dis <= 20.0) {
                    if (!temp) {
                        String msg = "[scarlet]ALERTA![]: **" + player.name + "** (" + player.getInfo().id + ") esta construindo um reator de torio em " + e.tile.x + ", " + e.tile.y + " a " + (int) dis + " tiles de distancia do nucleo";
                        new sendMsgToDiscord(bot, config, msg);
                        new sendLogMsgToDiscord(bot, config, msg);
                        Call.sendMessage("[red]Server[]: " + Util.handleDiscordMD(msg, true));
                        player.sendMessage("Voce [red]NAO[] construir esse bloco tao perto do nucleo");
                        temp = true;
                    }
                }
            }
        }
    }
}

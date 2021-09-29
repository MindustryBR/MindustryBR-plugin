package MindustryBR.Filters;

import MindustryBR.internal.util.Util;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class ReactorFilter {
    public static boolean exec(Administration.PlayerAction action) {
        if (action.type != Administration.ActionType.placeBlock) return true;
        if (action.block != Blocks.thoriumReactor) return true;

        Player player = action.player;
        Seq<CoreBlock.CoreBuild> cores = player.team().cores();
        Building reactor = action.block.buildType.get();

        for (CoreBlock.CoreBuild core : cores) {
            double dis = Util.distanceBetweenPoints(reactor.x, reactor.y, core.x, core.y);
            System.out.println("reactor: " + reactor.x + " " + reactor.y + "  core: " + core.x + " " + core.y);
            System.out.println(dis);
            if (dis <= 30.0) {
                player.sendMessage("Você não construir esse bloco tão perto do nucleo");
                return false;
            }
        }

        return true;
    }
}

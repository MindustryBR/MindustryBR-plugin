package MindustryBR.Filters;

import MindustryBR.internal.util.Util;
import arc.struct.Seq;
import mindustry.content.Blocks;
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
        Tile reactor = action.tile;
        boolean near = cores.contains(core -> Util.distanceBetweenPoints(reactor.x, reactor.y, core.tile.x, core.tile.y) <= 20);

        if (near) player.sendMessage("Voce [red]NAO[] construir esse bloco tao perto do nucleo");

        return !near;
    }
}

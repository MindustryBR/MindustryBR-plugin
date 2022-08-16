package MindustryBR.internal.classes.commands;

import arc.util.Nullable;
import mindustry.gen.Player;

public interface ClientCommand {
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = null;

    public static void run(String[] args, Player player) {};
}

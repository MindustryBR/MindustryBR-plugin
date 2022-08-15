package MindustryBR.internal.classes.commands;

import arc.util.Nullable;
import mindustry.gen.Player;

public interface ClientCommand {
    public static final boolean adminOnly = false;
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = null;

    public static void run(String[] args, Player player) {};
}

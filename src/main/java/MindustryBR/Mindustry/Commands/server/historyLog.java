package MindustryBR.Mindustry.Commands.server;

import MindustryBR.internal.classes.commands.ServerCommand;
import arc.util.Log;
import arc.util.Nullable;

import static MindustryBR.Main.logHistory;

public class historyLog implements ServerCommand {
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = "[MindustryBR] Toggle history log in console";

    public static void run(String[] args) {
        logHistory = !logHistory;
        if (logHistory) {
            Log.info("[MindustryBR] History will now be logged in console");
        } else {
            Log.info("[MindustryBR] History will not be logged in console");
        }
    }
}

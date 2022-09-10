package MindustryBR.Mindustry.Commands.server;

import MindustryBR.Mindustry.Commands.client.RTV;
import MindustryBR.internal.Classes.Commands.ServerCommand;
import arc.util.Log;
import arc.util.Nullable;


public class RTVServer implements ServerCommand {
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = "[MindustryBR] Toggle RTV command";

    public static void run(String[] args) {
        RTV.enable = !RTV.enable;
        if (RTV.enable) {
            Log.info("[MindustryBR] RTV command enabled");
        } else {
            Log.info("[MindustryBR] RTV command disabled");
        }
    }
}

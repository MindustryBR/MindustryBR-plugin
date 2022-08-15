package MindustryBR.Mindustry.Commands.server;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.classes.commands.ServerCommand;
import arc.Core;
import arc.util.Log;
import arc.util.Nullable;
import spark.Spark;

import static MindustryBR.Main.bot;
import static mindustry.Vars.net;

public class exit implements ServerCommand {
    @Nullable
    public static final String params = "";
    @Nullable
    public static final String desc = "[MindustryBR] Exit the server application and stops discord bot and API";

    public static void run(String[] args) {
        if (Bot.logged) {
            Log.info("[MindustryBR] Stopping discord bot...");
            bot.disconnect().join();
            bot = null;
        }

        Log.info("[MindustryBR] Stopping API...");
        Spark.stop();

        Log.info("[MindustryBR] Stopping server...");
        net.dispose();
        Core.app.exit();
    }
}

package MindustryBR.Mindustry.Commands.server;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.classes.commands.ServerCommand;
import arc.util.Log;
import arc.util.Nullable;

import static MindustryBR.Main.bot;
import static MindustryBR.Main.config;

public class startBot implements ServerCommand {
    @Nullable
    public static final String params = "[force]";
    @Nullable
    public static final String desc = "[MindustryBR] Start the discord bot if it isn't already online";

    public static void run(String[] args) {
        // Start the discord bot if token was provided and the bot isn't online
        if (((!config.isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) || args.length > 0) && !Bot.logged) {
            if (args.length > 0) Log.info("force starting bot");
            bot = Bot.run();
        }
    }

    ;
}

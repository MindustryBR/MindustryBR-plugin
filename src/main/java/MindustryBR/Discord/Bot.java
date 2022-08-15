package MindustryBR.Discord;

import MindustryBR.Discord.CustomListeners.MsgCreate;
import arc.util.Log;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import static MindustryBR.Main.config;

public class Bot {
    public static boolean logged = false;

    public static DiscordApi run() {
        DiscordApi bot = new DiscordApiBuilder()
                .setToken(config.getJSONObject("discord").getString("token"))
                .setAllIntents()
                .login().join();

        bot.setAutomaticMessageCacheCleanupEnabled(true);
        bot.setMessageCacheSize(0, 0);

        // Add custom MessageCreateListener
        bot.addListener(new MsgCreate());

        // Print logged account
        Log.info("Bot logged in as " + bot.getYourself().getDiscriminatedName());
        System.out.println("Bot logged in as " + bot.getYourself().getDiscriminatedName());

        logged = true;

        return bot;
    }

}
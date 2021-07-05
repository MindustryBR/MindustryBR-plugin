package MindustryBR.Discord;

import MindustryBR.Discord.CustomListeners.MsgCreate;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import static MindustryBR.MindustryBR.config;

public class Bot {
    public static boolean logged = false;

    public static DiscordApi run() {
        DiscordApi bot = new DiscordApiBuilder()
                .setToken(config.getJSONObject("discord").getString("token"))
                .setAllIntents()
                .login().join();

        bot.setAutomaticMessageCacheCleanupEnabled(true);
        bot.setMessageCacheSize(10, 60);

        // Add custom MessageCreateListener
        bot.addListener(new MsgCreate(bot));

        // Print logged account
        System.out.println("Bot logged in as " + bot.getYourself().getDiscriminatedName());

        logged = true;

        return bot;
    }

}
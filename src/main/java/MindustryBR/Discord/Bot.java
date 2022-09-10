package MindustryBR.Discord;

import MindustryBR.Discord.CustomListeners.MsgCreate;
import arc.util.Log;
import arc.util.Nullable;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import static MindustryBR.Main.config;

public class Bot {
    public static boolean logged = false;

    @Nullable
    public static DiscordApi run() {
        String token = config.getJSONObject("discord").getString("token");
        if (token == null || token.isBlank() || token.equals("placeholder")) return null;

        DiscordApi bot = new DiscordApiBuilder()
                .setToken(token)
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
package MindustryBR.DiscordBot;

import MindustryBR.DiscordBot.CustomListeners.MsgCreate;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import static MindustryBR.MindustryBR.config;

public class Bot {
    public static boolean logged = false;

    public static DiscordApi run() {
        DiscordApi api = new DiscordApiBuilder().setToken(config.getJSONObject("discord").getString("token")).login().join();

        // Add custom MessageCreateListener
        api.addListener(new MsgCreate(api));

        // Print logged account
        System.out.println("Bot logged in as " + api.getYourself().getDiscriminatedName());

        logged = true;

        return api;
    }

}
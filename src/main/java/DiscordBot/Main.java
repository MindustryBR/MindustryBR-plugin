package DiscordBot;

// Arc imports
import arc.Core;
import arc.util.*;

// Mindustry imports
import mindustry.gen.Call;

// Javacord imports
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.*;

// Org.json imports
import org.json.*;

public class Main {
    //public static void main(String[] args) {
    public static DiscordApi run(JSONObject config) {
        DiscordApi api = new DiscordApiBuilder().setToken(config.getJSONObject("discord").getString("token")).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!!ping")) {
                event.getChannel().sendMessage("Pong!");
            }

            ServerTextChannel channel = event.getServerTextChannel().get();
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                String name = "";
                if (event.getMessageAuthor().getDisplayName().toLowerCase().contains("admin") || event.getMessageAuthor().getDisplayName().toLowerCase().contains("adm")) {
                    name = "retardado";
                } else if (event.getMessageAuthor().getDisplayName().toLowerCase().contains("dono")) {
                    name = "retardado²";
                } else {
                    name = event.getMessageAuthor().getDisplayName();
                }

                Call.sendMessage("[orange][[[]" + name + "[orange]]:[] " + event.getMessage().getReadableContent().replace("\n", " "));
                Log.info("DISCORD > [" + event.getMessageAuthor().getDisplayName() + "]: " + event.getMessage().getReadableContent().replace("\n", " "));
            }
        });

        // Print the invite url of your bot
        System.out.println("Bot logged in as " + api.getYourself().getDiscriminatedName());

        return api;
    }

}
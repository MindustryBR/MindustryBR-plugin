package MindustryBR.Mindustry.Commands.server;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.Classes.Commands.ServerCommand;
import arc.Core;
import arc.util.Log;
import arc.util.Nullable;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import spark.Spark;

import java.awt.*;
import java.util.Optional;

import static MindustryBR.Main.bot;
import static MindustryBR.Main.config;
import static mindustry.Vars.net;

public class exit implements ServerCommand {
    @Nullable
    public static final String params = "";
    @Nullable
    public static final String desc = "[MindustryBR] Exit the server application and stops discord bot and API";

    public static void run(String[] args) {
        Log.info("[MindustryBR] Stopping API...");
        Spark.stop();

        Log.info("[MindustryBR] Stopping server...");
        net.dispose();
        Core.app.exit();

        if (Bot.logged) {
            Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));
            if (optionalChannel.isEmpty()) return;
            ServerTextChannel channel = optionalChannel.get();

            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle("Servidor **" + config.getJSONObject("discord").getString("name") + "** offline!")
                            .setColor(Color.RED)
                            .setDescription("O servidor foi desligado.")
                            .setTimestampToNow())
                    .send(channel);

            Log.info("[MindustryBR] Stopping discord bot...");
            bot.disconnect().join();
            bot = null;
        }
    }
}

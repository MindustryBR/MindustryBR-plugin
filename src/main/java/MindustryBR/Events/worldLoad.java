package MindustryBR.Events;

import mindustry.game.EventType;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Optional;

public class worldLoad {
    private static boolean started = false;

    public static void run (DiscordApi bot, JSONObject config, EventType.WorldLoadEvent e) {
        if(started) return;

        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));

        if (optionalChannel.isEmpty()) return;

        ServerTextChannel channel = optionalChannel.get();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Servidor online!")
                .setColor(Color.green)
                .setDescription("**IP:** `mindustryptbr.ddns.net`");

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();

        started = true;
    }
}

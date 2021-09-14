package MindustryBR.Discord.Commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.awt.*;

import static MindustryBR.Events.worldLoad.started;

public class ip {
    public ip(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        EmbedBuilder embed = new EmbedBuilder()
                .setTimestampToNow();

        if (started) {
            embed.setTitle("IP do servidor")
                .setDescription("```\n" + config.getString("ip") + "\n```")
                .setColor(Color.green);
        } else {
            embed.setDescription("**Servidor fechado**")
                .setColor(Color.red);
        }


        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

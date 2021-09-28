package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.Util;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

public class HostStatus {
    public HostStatus(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) throws Exception {
        ServerTextChannel channel = event.getServerTextChannel().get();
        EmbedBuilder embed = new EmbedBuilder()
                .setTimestampToNow();

        embed.setTitle("Estatisticas da Host")
                .addField("CPU", Util.getProcessCpuLoad() + "%")
                .addField("Memoria", Util.getMemoryUsage())
                .setTimestampToNow()
                .setColor(Util.randomColor());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}
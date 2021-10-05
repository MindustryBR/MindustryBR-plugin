package MindustryBR.Discord.Commands;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.util.Util;
import arc.util.Strings;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import static MindustryBR.Main.worldHistory;

public class historyDC {
    public historyDC(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        int x = -1, y = -1;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {
            new MessageBuilder()
                    .append("Coordenadas invalidas")
                    .send(channel)
                    .join();
            return;
        }

        if (x < 0 || x > worldHistory.length || y < 0 || y > worldHistory[0].length) {
            new MessageBuilder()
                    .append("Coordenadas invalidas")
                    .send(channel)
                    .join();
            return;
        }

        LimitedQueue<BaseEntry> tileHistory = worldHistory[x][y];

        StringBuilder message = new StringBuilder();

        if (tileHistory.isOverflown()) message.append("\n... historico muito grande");
        for (BaseEntry historyEntry : tileHistory) message.append("\n").append(Strings.stripColors(historyEntry.getMessage()));
        if (tileHistory.isEmpty()) message.append("\n~ sem historico");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Historico do bloco (" + x + "," + y + ")")
                .setDescription("```diff\n" + message + "\n```")
                .setTimestampToNow()
                .setColor(Util.randomColor());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel)
                .join();
    }
}

package MindustryBR.Discord.Commands;

import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import MindustryBR.internal.util.Util;
import arc.struct.ObjectSet;
import arc.util.Strings;
import mindustry.net.Administration;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import static MindustryBR.Main.playerHistory;
import static MindustryBR.Main.worldHistory;
import static mindustry.Vars.netServer;

public class PlayerHistoryDC {
    public PlayerHistoryDC(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        ObjectSet<Administration.PlayerInfo> players = null;

        if (netServer.admins.findByName(args[1]).size > 0 || netServer.admins.searchNames(args[1]).size > 0) {
            players = netServer.admins.findByName(args[1]);
            if (players.size < 1) players = netServer.admins.searchNames(args[1]);
        } else {
            new MessageBuilder()
                    .append("Nao achei nenhum jogador com essas informacoes")
                    .send(channel)
                    .join();
        }

        if (players == null || playerHistory.get(players.first().id) == null) {
            new MessageBuilder()
                    .append("Nao achei nenhum jogador com essas informacoes")
                    .send(channel)
                    .join();
            return;
        }

        LimitedQueue<BaseEntry> history = playerHistory.get(players.first().id);

        StringBuilder message = new StringBuilder();

        if (history.isOverflown()) message.append("\n... historico muito grande");
        for (BaseEntry historyEntry : history) message.append("\n").append(Strings.stripColors(historyEntry.getMessage(false)));
        if (history.isEmpty()) message.append("\n~ sem historico");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Historico do jogador " + players.first().lastName)
                .setDescription("```diff\n" + message + "\n```")
                .setTimestampToNow()
                .setColor(Util.randomColor());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel)
                .join();
    }
}

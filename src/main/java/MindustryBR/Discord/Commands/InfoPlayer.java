package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.Util;
import arc.struct.ObjectSet;
import mindustry.net.Administration;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.io.IOException;

import static mindustry.Vars.netServer;

public class InfoPlayer {
    public InfoPlayer(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) throws IOException {
        ServerTextChannel channel = event.getServerTextChannel().get();

        if (netServer.admins.findByName(args[1]).size > 0 || netServer.admins.searchNames(args[1]).size > 0) {
            ObjectSet<Administration.PlayerInfo> players = netServer.admins.findByName(args[1]);

            if (players.size <= 1) players = netServer.admins.searchNames(args[1]);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestampToNow()
                    .setColor(Util.randomColor())
                    .setTitle("Informacoes dos jogadores")
                    .setDescription(players.size + " jogador(es) encontrado(s)");

            int i = 0;
            for (Administration.PlayerInfo info : players) {
                if (i < 21) {
                    String country = Util.ip2country(info.lastIP);

                    embed.addInlineField((i + 1) + " - " + info.lastName, "UUID: `" + info.id + "`\n" +
                            "Nomes usados: `" + info.names.toString(", ") + "`\n" +
                            "Pais: `" + country + "`\n" +
                            "Entrou " + info.timesJoined + " vez(es)\n" +
                            "Kickado " + info.timesKicked + " vez(es)\n");
                }

                i++;
            }

            new MessageBuilder()
                    .setEmbed(embed)
                    .send(channel)
                    .join();
        } else {
            new MessageBuilder()
                    .append("Nao achei nenhum jogador com essas informacoes")
                    .send(channel)
                    .join();
        }
    }
}

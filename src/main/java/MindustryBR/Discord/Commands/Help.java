package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.Util;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

public class Help {
    public Help(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        String prefix = config.getJSONObject("discord").getString("prefix");

        EmbedBuilder embed= new EmbedBuilder()
                .setTitle("Comandos")
                .setDescription(prefix + "help - Tu ja ta olhando ele\n" +
                        prefix + "gameinfo - Mostra as informacoes do jogo atual\n" +
                        prefix + "ip - Mostra o IP do servidor\n" +
                        prefix + "banplayer <Nome|ID> - Bane um jogador pelo nome ou ID\n" +
                        prefix + "unbanplayer <ID> - Desbane um jogador pelo ID\n" +
                        prefix + "kickplayer <Nome> - Kicka um jogador pelo nome\n" +
                        prefix + "pardonplayer <ID> - Perdoa o kick de um jogador pelo ID\n" +
                        prefix + "playerinfo <Nome|ID> - Pesquisa e mostra informacoes dos jogadores\n")
                .setColor(Util.randomColor())
                .setTimestampToNow();

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel)
                .join();
    }
}

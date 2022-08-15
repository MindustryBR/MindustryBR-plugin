package MindustryBR.Discord.Commands;

import MindustryBR.internal.Util;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import static MindustryBR.Main.config;

public class Help {
    public Help(MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        String prefix = config.getJSONObject("discord").getString("prefix");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Comandos")
                .setDescription(prefix + "help - Tu ja ta olhando ele\n" +
                        prefix + "playerhistory <nome> - Mostra o historico do jogador\n" +
                        prefix + "history <x> <y> - Mostra o historico do bloco na coordenada informada\n" +
                        prefix + "gameinfo - Mostra as informacoes do jogo atual\n" +
                        prefix + "ip - Mostra o IP do servidor\n" +
                        prefix + "banplayer <type - id|name|ip> <id|name|ip> - Bane um jogador pelo nome ou ID\n" +
                        prefix + "unbanplayer <ID> - Desbane um jogador pelo ID\n" +
                        prefix + "kickplayer <type - id|name|ip> <id|name|ip> - Kicka um jogador pelo nome\n" +
                        prefix + "pardonplayer <ID> - Perdoa o kick de um jogador pelo ID\n" +
                        prefix + "playerinfo <Nome|ID> - Pesquisa e mostra informacoes dos jogadores\n" +
                        prefix + "status - Mostra o status da host\n")
                .setColor(Util.randomColor())
                .setTimestampToNow();

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel)
                .join();
    }
}

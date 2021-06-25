package MindustryBR.DiscordBot.Commands;

import static mindustry.Vars.*;

import MindustryBR.util.Util;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.*;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.Optional;

public class GameInfo {
    public GameInfo(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        String stats = "Wave: " + state.wave +
                "\nTempo de jogo: " + Util.msToDuration(state.stats.timeLasted) +
                "\nInimigos vivos: " + state.enemies +
                "\nInimigos mortos: " + state.stats.enemyUnitsDestroyed +
                "\nBlocos construidos: " + state.stats.buildingsBuilt +
                "\nBlocos descontruidos: " + state.stats.buildingsDeconstructed +
                "\nBlocos destruidos: " + state.stats.buildingsDestroyed;

        String map = "Nome: " + state.map.name() +
                "\nAutor: " + state.map.author() +
                "\nTamanho: " + state.map.width + "x" + state.map.height +
                "\nDescricao: " + state.map.description();

        StringBuilder players = new StringBuilder();
        if (Groups.player.size() > 1) {
            for (Player p : Groups.player) {
                players.append(p.name).append("\n");
            }
        } else players.append("Nenhum jogador");

        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle("Estatisticas do jogo atual")
                        .setColor(Util.randomColor())
                        .setDescription(stats)
                        .addInlineField("Jogadores", players.toString())
                        .addInlineField("Mapa", map))
                .send(channel).join();
    }
}

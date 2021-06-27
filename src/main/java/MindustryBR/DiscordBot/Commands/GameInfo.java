package MindustryBR.DiscordBot.Commands;

import static mindustry.Vars.state;

import MindustryBR.util.Util;
import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.*;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.io.File;

public class GameInfo {
    public GameInfo(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        if (args.length > 1 && args[1].equalsIgnoreCase("raw")) {
            String raw = "> Estatisticas" +
                    "\nWave: " + state.wave +
                    "\nTempo de jogo: " + state.stats.timeLasted + " / " + Util.msToDuration(state.stats.timeLasted) +
                    "\nInimigos vivos: " + state.enemies +
                    "\nInimigos mortos: " + state.stats.enemyUnitsDestroyed +
                    "\nBlocos construidos: " + state.stats.buildingsBuilt +
                    "\nBlocos descontruidos: " + state.stats.buildingsDeconstructed +
                    "\nBlocos destruidos: " + state.stats.buildingsDestroyed +
                    "\n\n> Mapa" +
                    "\nNome: " + state.map.name() +
                    "\nAutor: " + state.map.author() +
                    "\nTamanho: " + state.map.width + "x" + state.map.height +
                    "\nDescricao: " + state.map.description();
            MessageBuilder msgBuilder = new MessageBuilder()
                    .append(raw)
                    .append("\n\n\n" + state.map.previewFile().absolutePath());

            if (state.map.previewFile().exists()) msgBuilder.addAttachment(new File(state.map.previewFile().absolutePath()));

            msgBuilder.send(channel);
            return;
        }

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
        if (Groups.player.size() > 0) {
            for (Player p : Groups.player) {
                players.append(Strings.stripColors(p.name)).append("\n");
            }
        } else players.append("Nenhum jogador");

        EmbedBuilder embed= new EmbedBuilder()
                .setTitle("Estatisticas do jogo atual")
                .setColor(Util.randomColor())
                .setDescription(stats)
                .addInlineField("Mapa", map)
                .addInlineField("Jogadores", players.toString());

        if (state.map.previewFile().exists()) embed.setImage(state.map.previewFile().absolutePath());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

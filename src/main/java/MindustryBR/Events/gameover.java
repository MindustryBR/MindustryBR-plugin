package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import mindustry.game.EventType.GameOverEvent;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Optional;

import static mindustry.Vars.state;
import static MindustryBR.Discord.Commands.GameInfo.stats;

public class gameover {
    public static void run(DiscordApi bot, JSONObject config, GameOverEvent e) {
        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));

        if (optionalChannel.isEmpty()) return;

        ServerTextChannel channel = optionalChannel.get();

        String wave = "Wave: " + state.wave +
                "\nInimigos vivos: " + state.enemies;

        String statsStr = "Unidades construidas: " + stats.unitsBuilt +
                "\nUnidades destruidas: " + stats.unitsDestroyed +
                "\nConstrucoes construidas: " + stats.buildingsConstructed +
                "\nConstrucoes descontruidas: " + stats.buildingsDesconstructed +
                "\nConstrucoes destruidas: " + stats.buildingsDestroyed;

        String map = "Nome: " + state.map.name() +
                "\nAutor: " + state.map.author() +
                "\nTamanho: " + state.map.width + "x" + state.map.height +
                "\nDescricao: " + state.map.description();

        StringBuilder players = new StringBuilder();
        if (Groups.player.size() > 0) {
            for (Player p : Groups.player) {
                players.append(Util.handleName(p, true)).append(", ");
            }
        } else players.append("Nenhum jogador");

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Gameover!")
                .setColor(Color.red)
                .setDescription(wave)
                .addInlineField("Estatisticas", statsStr)
                .addInlineField("Mapa", map)
                .addField("Jogadores", players.toString());

        if (state.map.previewFile().exists()) embed.setImage(state.map.previewFile().file());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

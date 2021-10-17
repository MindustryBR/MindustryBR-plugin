package MindustryBR.Discord.Commands;

import MindustryBR.internal.classes.Stats;
import MindustryBR.internal.Util;
import arc.util.Strings;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.modules.ItemModule;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.Optional;

import static MindustryBR.internal.Util.getLocalized;
import static mindustry.Vars.state;

public class GameInfo {
    public static Stats stats = new Stats();

    public GameInfo(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        Server emojiBank = bot.getServerById(config.getJSONObject("discord").getString("emoji-bank")).get();

        Teams.TeamData data = Groups.player.size() > 0 ? Groups.player.first().team().data() : state.teams.get(Team.sharded);
        CoreBlock.CoreBuild core = data.cores.first();
        ItemModule items = core.items;

        StringBuilder res = new StringBuilder();
        items.each((arg1, arg2) -> {
            Optional<KnownCustomEmoji> emoji = emojiBank.getCustomEmojisByName(arg1.name.replaceAll("-", "_")).stream().findFirst();

            emoji.ifPresentOrElse(
                    knownCustomEmoji -> res.append(knownCustomEmoji.getMentionTag()).append(" "),
                    () -> {});

            res.append(Strings.stripColors(getLocalized(arg1.name)))
                    .append(": ")
                    .append(items.get(arg1.id))
                    .append("\n");
        });

        String waves = "Wave: " + state.wave +
                "\nProxima wave em " + Util.msToDuration((state.wavetime / 60) * 1000, true) +
                "\nInimigos vivos: " + state.enemies;

        String statsStr = "Unidades construidas: " + stats.unitsBuilt +
                "\nUnidades destruidas: " + stats.unitsDestroyed +
                "\nConstrucoes construidas: " + stats.buildingsConstructed +
                "\nConstrucoes descontruidas: " + stats.buildingsDeconstructed +
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
                .setColor(Util.randomColor())
                .setTimestampToNow()
                .setTitle("Informacoes do jogo atual")
                .setDescription(waves)
                .addInlineField("Estatisticas", statsStr)
                .addField("Mapa", map)
                .addField("Jogadores", players.toString());

        EmbedBuilder resEmbed = new EmbedBuilder()
                .setColor(Util.randomColor())
                .setTimestampToNow()
                .setTitle("Recursos")
                .setDescription(res.toString());

        if (state.map.previewFile().exists()) embed.setImage(state.map.previewFile().file());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();

        new MessageBuilder()
                .setEmbed(resEmbed)
                .send(channel)
                .join();
    }
}

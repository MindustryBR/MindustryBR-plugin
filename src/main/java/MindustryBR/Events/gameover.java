package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import arc.util.Log;
import mindustry.game.EventType.GameOverEvent;
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
import org.json.JSONObject;

import java.awt.*;
import java.util.Optional;

import static mindustry.Vars.state;

public class gameover {
    public static void run(DiscordApi bot, JSONObject config, GameOverEvent e) {
        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));

        if (optionalChannel.isEmpty()) return;

        ServerTextChannel channel = optionalChannel.get();

        /*
        // Default player team
        Teams.TeamData data = state.teams.get(Team.sharded);
        // Items are shared between cores, so it doesnt matter which one we get
        CoreBlock.CoreBuild core = data.cores.first();
        ItemModule items = core.items;
        String[] resourcesName = Util.resourcesName;
        short[] resourcesID = Util.resourcesID;
        */

        String stats = "Wave: " + state.wave +
                "\nInimigos vivos: " + state.enemies;

        /*
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < resourcesName.length; i++) {
            Optional<KnownCustomEmoji> emoji = bot.getCustomEmojiById(Util.getResourceEmojiID(resourcesID[i], config));
            res.append(emoji.map(knownCustomEmoji -> knownCustomEmoji.getMentionTag() + " ").orElse("")).append(resourcesName[i]).append(": ").append(items.get(resourcesID[i])).append("\n");
        }
        */

        String map = "Nome: " + state.map.name() +
                "\nAutor: " + state.map.author() +
                "\nTamanho: " + state.map.width + "x" + state.map.height +
                "\nDescricao: " + state.map.description();

        StringBuilder players = new StringBuilder();
        if (Groups.player.size() > 0) {
            for (Player p : Groups.player) {
                players.append(Util.handleName(p, true)).append("\n");
            }
        } else players.append("Nenhum jogador");

        EmbedBuilder embed= new EmbedBuilder()
                .setTitle("Gameover!")
                .setColor(Color.red)
                .setDescription(stats)
                //.addInlineField("Recursos", res.toString())
                .addInlineField("Mapa", map)
                .addInlineField("Jogadores", players.toString());

        if (state.map.previewFile().exists()) embed.setImage(state.map.previewFile().file());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

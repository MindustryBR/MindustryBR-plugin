package MindustryBR.DiscordBot.Commands;

import MindustryBR.util.ContentHandler;
import MindustryBR.util.Net;
import MindustryBR.util.Util;
import arc.files.Fi;
import arc.util.io.Streams;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.*;

import static mindustry.Vars.state;

public class GameInfo {
    public GameInfo(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) throws IOException {
        ServerTextChannel channel = event.getServerTextChannel().get();


        //--------------------------------------------------------------------
        // idk wtf im doing here
        ContentHandler.Map map1 = ContentHandler.readMap(new DataInputStream(new FileInputStream(state.map.file.file())));
        new File("cache/").mkdir();
        File mapFile = new File("cache/" + state.map.file.name());
        Fi imageFile = Fi.get("cache/image_" + state.map.file.name().replace(".msav", ".png"));
        Streams.copy(new DataInputStream(new FileInputStream(state.map.file.file())), new FileOutputStream(mapFile));
        ImageIO.write(map1.image, "png", imageFile.file());
        //------------------------------------------------------------------------

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

            if (imageFile.exists()) msgBuilder.addAttachment(imageFile.file());

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
                players.append(Util.handleName(p, true)).append("\n");
            }
        } else players.append("Nenhum jogador");

        EmbedBuilder embed= new EmbedBuilder()
                .setTitle("Estatisticas do jogo atual")
                .setColor(Util.randomColor())
                .setDescription(stats)
                .addInlineField("Mapa", map)
                .addInlineField("Jogadores", players.toString());

        if (imageFile.exists()) embed.setImage(imageFile.file());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

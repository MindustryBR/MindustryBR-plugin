package MindustryBR.DiscordBot.Commands;

import MindustryBR.internal.util.Util;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;
import mindustry.world.modules.ItemModule;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import static mindustry.Vars.state;

public class GameInfo {
    public GameInfo(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        // Default player team
        Teams.TeamData data = state.teams.get(Team.sharded);
        // Items are shared between cores, so it doesnt matter which one we get
        CoreBuild core = data.cores.first();
        ItemModule items = core.items;

        String[] resourcesName = Util.resourcesName;
        short[] resourcesID = Util.resourcesID;

        if (args.length > 1 && args[1].equalsIgnoreCase("raw")) {
            StringBuilder players = new StringBuilder();
            if (Groups.player.size() > 0) {
                for (Player p : Groups.player) {
                    players.append(Util.handleName(p, true)).append("\n");
                }
            } else players.append("Nenhum jogador");

            String raw = "> Estatisticas" +
                    "\nWave: " + state.wave +
                    "\nProxima wave em " + Math.round(state.wavetime / 60) + " segundos" +
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
                    "\nDescricao: " + state.map.description() +
                    "\n\n> Jogadores" +
                    players.toString();

            MessageBuilder msgBuilder = new MessageBuilder()
                    .append(raw)
                    .append("\n\n\n" + state.map.previewFile().absolutePath());

            if (state.map.previewFile().exists()) msgBuilder.addAttachment(state.map.previewFile().file());

            msgBuilder.send(channel);

            // Resource info
            MessageBuilder msgBuilder2 = new MessageBuilder();
            if (!state.rules.waves) {
                channel.sendMessage("Only available in survival mode!");
                return;
            }

            for(int i = 0; i < resourcesName.length; i++) {
                msgBuilder2.append(Util.getResourceEmoji(resourcesID[i], config) + " " + resourcesName[i] + ": " + items.get(resourcesID[i]) + "\n");
            }

            msgBuilder2.send(channel);
            return;
        }

        String stats = "Wave: " + state.wave +
                "\nProxima wave em " + Math.round(state.wavetime / 60) + " segundos" +
                //"\nTempo de jogo: " + Util.msToDuration(state.stats.timeLasted) +
                "\nInimigos vivos: " + state.enemies;
                //"\nInimigos mortos: " + state.stats.enemyUnitsDestroyed +
                //"\nBlocos construidos: " + state.stats.buildingsBuilt +
                //"\nBlocos descontruidos: " + state.stats.buildingsDeconstructed +
                //"\nBlocos destruidos: " + state.stats.buildingsDestroyed;

        StringBuilder res = new StringBuilder();
        for(int i = 0; i < resourcesName.length; i++) {
            res.append(Util.getResourceEmoji(resourcesID[i], config)).append(" ").append(resourcesName[i]).append(": ").append(items.get(resourcesID[i])).append("\n");
        }

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
                .addInlineField("Recursos", res.toString())
                .addInlineField("Mapa", map)
                .addInlineField("Jogadores", players.toString());

        if (state.map.previewFile().exists()) embed.setImage(state.map.previewFile().file());

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

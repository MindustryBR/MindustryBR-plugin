package MindustryBR.Events;

import MindustryBR.internal.classes.Stats;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Optional;

import static MindustryBR.Discord.Commands.GameInfo.stats;
import static MindustryBR.Main.worldHistory;
import static mindustry.Vars.state;

public class worldLoad {
    public static boolean started = false;

    public static void run (DiscordApi bot, JSONObject config, EventType.WorldLoadEvent e) {
        stats = new Stats();

        // Unpause the game if one or more player is connected
        if (Groups.player.size() >= 1 && state.serverPaused) state.serverPaused = false;

        worldHistory = new LimitedQueue[Vars.world.width()][Vars.world.height()];

        for (int x = 0; x < Vars.world.width(); x++) {
            for (int y = 0; y < Vars.world.height(); y++) {
                worldHistory[x][y] = new LimitedQueue<>();
            }
        }

        if(started) return;
        started = true;

        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));
        if (optionalChannel.isEmpty()) return;
        ServerTextChannel channel = optionalChannel.get();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Servidor online!")
                .setColor(Color.green)
                .setDescription("**IP:** `"+ config.getString("ip") + "`");

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();
    }
}

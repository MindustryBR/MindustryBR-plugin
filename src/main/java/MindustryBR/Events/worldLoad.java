package MindustryBR.Events;

import MindustryBR.internal.classes.Stats;
import MindustryBR.internal.util.Util;
import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Optional;

import static mindustry.Vars.state;
import static MindustryBR.Discord.Commands.GameInfo.stats;

public class worldLoad {
    private static boolean started = false;

    public static void run (DiscordApi bot, JSONObject config, EventType.WorldLoadEvent e) {
        stats = new Stats();

        // Unpause the game if one or more player is connected
        if (Groups.player.size() >= 1 && state.serverPaused) state.serverPaused = false;

        if(started) return;

        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));

        if (optionalChannel.isEmpty()) return;

        ServerTextChannel channel = optionalChannel.get();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Servidor online!")
                .setColor(Color.green)
                .setDescription("**IP:** `mindustryptbr.ddns.net`");

        new MessageBuilder()
                .setEmbed(embed)
                .send(channel).join();

        started = true;
    }
}

package MindustryBR.Events;

import arc.util.Log;
import arc.util.Strings;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Groups;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Optional;

import static mindustry.Vars.state;

public class playerLeave {
    public static void run(DiscordApi bot, JSONObject config, PlayerLeave e) {
        // Pause the game if no one is connected
        if (Groups.player.size()-1 < 1) {
            state.serverPaused = true;
            Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
        }


        // Send disconnect message to discord
        if (!config.getJSONObject("discord").getString("token").isBlank()) {
            Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));
            Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("log_channel_id"));

            String msg = ":outbox_tray: " + Strings.stripColors(e.player.name) + " desconectou";

            if (optionalChannel.isPresent()) {
                ServerTextChannel channel = optionalChannel.get();

                channel.sendMessage(msg);
            } else {
                Log.info("[MindustryBR] The channel id provided is invalid or the channel is unreachable");
            }

            if (optionalLogChannel.isPresent()) {
                ServerTextChannel logChannel = optionalLogChannel.get();

                logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
            } else {
                Log.info("[MindustryBR] The log channel id provided is invalid or the channel is unreachable");
            }
        }
    }
}

package MindustryBR.util;

import arc.util.Log;
import arc.util.Strings;
import mindustry.game.EventType.PlayerChatEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Optional;

public class sendMsgToDiscord {
    public sendMsgToDiscord(DiscordApi bot, JSONObject config, PlayerChatEvent e) {
        String msg = "**" + Strings.stripColors(e.player.name) + "**: " + e.message;
        JSONObject discordConfig = config.getJSONObject("discord");

        // Check if log_channel_id is not blank
        if (!discordConfig.getString("log_channel_id").isBlank()) {
            // Check if the message is a command
            if (e.message.startsWith("/")) {
                Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(discordConfig.getString("log_channel_id"));

                // If the log channel exists, send message
                if (optionalLogChannel.isPresent()) {
                    optionalLogChannel.get().sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
                } else Log.info("[MindustryBR] The log channel id is invalid or the channel is unreachable");
            }
        }

        // Check if log_channel_id is not blank
        if (!discordConfig.getString("channel_id").isBlank()) {
            // Check if the message isn't a command
            if (!e.message.startsWith("/")) {
                Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(discordConfig.getString("channel_id"));

                // If the channel exists, send message
                if (optionalChannel.isPresent()) {
                    optionalChannel.get().sendMessage(msg);
                } else Log.info("[MindustryBR] The channel id is invalid or the channel is unreachable");
            }
        }
    }
}

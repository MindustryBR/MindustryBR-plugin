package MindustryBR.internal.util;

import arc.util.Log;
import arc.util.Strings;
import mindustry.game.EventType.PlayerChatEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Optional;

public class sendLogMsgToDiscord {
    /**
     *
     * @param bot Discord bot
     * @param config Plugin config
     * @param e Event
     */
    public sendLogMsgToDiscord(DiscordApi bot, JSONObject config, PlayerChatEvent e) {
        String msg = "(" + e.player.getInfo().id + ") **" + e.player.name + "**: " + e.message;
        msg = Strings.stripColors(msg);

        JSONObject discordConfig = config.getJSONObject("discord");

        // Check if log_channel_id is not blank
        if (!discordConfig.getString("log_channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(discordConfig.getString("log_channel_id"));

            // If the log channel exists, send message
            if (optionalLogChannel.isPresent()) {
                optionalLogChannel.get().sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
            } else Log.info("[MindustryBR] The log channel id is invalid or the channel is unreachable");
        }
    }

    /**
     *
     * @param bot Discord bot
     * @param config Plugin config
     * @param name Player name
     * @param message Message
     */
    public sendLogMsgToDiscord(DiscordApi bot, JSONObject config, String name, String message) {
        String msg = "**" + name + "**: " + message;
        msg = Strings.stripColors(msg);

        JSONObject discordConfig = config.getJSONObject("discord");

        // Check if log_channel_id is not blank
        if (!discordConfig.getString("log_channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(discordConfig.getString("log_channel_id"));

            // If the log channel exists, send message
            if (optionalLogChannel.isPresent()) {
                optionalLogChannel.get().sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
            } else Log.info("[MindustryBR] The log channel id is invalid or the channel is unreachable");
        }
    }

    /**
     *
     * @param bot Discord bot
     * @param config Plugin config
     * @param message Message
     */
    public sendLogMsgToDiscord(DiscordApi bot, JSONObject config, String message) {
        message = Strings.stripColors(message);

        JSONObject discordConfig = config.getJSONObject("discord");

        // Check if log_channel_id is not blank
        if (!discordConfig.getString("log_channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(discordConfig.getString("log_channel_id"));

            // If the log channel exists, send message
            if (optionalLogChannel.isPresent()) {
                optionalLogChannel.get().sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + message);
            } else Log.info("[MindustryBR] The log channel id is invalid or the channel is unreachable");
        }
    }
}

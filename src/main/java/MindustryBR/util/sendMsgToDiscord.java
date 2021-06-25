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

        if(!config.getJSONObject("discord").getString("channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));

            if (optionalChannel.isPresent() && !e.message.startsWith("/")) {
                ServerTextChannel channel = optionalChannel.get();

                channel.sendMessage(msg);
            } else {
                Log.info("[MindustryBR] The channel id provided is invalid or the channel is unreachable");
            }
        }

        if(!config.getJSONObject("discord").getString("log_channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("log_channel_id"));

            if (optionalLogChannel.isPresent()) {
                ServerTextChannel logChannel = optionalLogChannel.get();

                logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
            } else {
                Log.info("[MindustryBR] The log channel id provided is invalid or the channel is unreachable");
            }
        }

        /*
        Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("channel_id"));
        Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(config.getJSONObject("discord").getString("log_channel_id"));


        String msg = "**" + Strings.stripColors(e.player.name) + "**: " + e.message;

        if (optionalChannel.isPresent() && !e.message.startsWith("/")) {
            ServerTextChannel channel = optionalChannel.get();

            channel.sendMessage(msg);
        } else {
            Log.info("[MindustryBR.MindustryBR] The channel id provided is invalid or the channel is unreachable");
        }

        if (optionalLogChannel.isPresent()) {
            ServerTextChannel logChannel = optionalLogChannel.get();

            logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
        } else {
            Log.info("[MindustryBR.MindustryBR] The log channel id provided is invalid or the channel is unreachable");
        }
        */
    }
}

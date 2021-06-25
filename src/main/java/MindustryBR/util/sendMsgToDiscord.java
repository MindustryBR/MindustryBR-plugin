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
    }
}

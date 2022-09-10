package MindustryBR.internal;

import MindustryBR.Discord.Bot;
import arc.util.Log;
import arc.util.Strings;
import mindustry.game.EventType.PlayerChatEvent;
import mindustry.gen.Call;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static MindustryBR.Main.bot;
import static MindustryBR.Main.config;

public class DiscordRelay {
    // Send message to game server chat
    public static void sendMsgToGame(MessageCreateEvent event) {
        if (!Bot.logged) return;
        if (event.getServerTextChannel().isPresent()) {
            String name = Util.handleName(event.getMessageAuthor().getDisplayName(), false, true);
            String msg = event.getMessage().getReadableContent().replace("\n", " ");
            msg = Util.handleDiscordMD(msg);

            Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
            Log.info("DISCORD > [" + name + "]: " + msg);
        }
    }

    public static void sendMsgToGame(String author, String msg) {
        if (!Bot.logged) return;
        String name = Util.handleName(author, false, true);
        name = Util.handleDiscordMD(name, true);
        msg = Util.handleDiscordMD(msg, true);

        Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
        Log.info("DISCORD > [" + name + "]: " + msg);
    }

    // Send message to Discord server

    /**
     * @param message Message
     */
    public static void sendMsgToDiscord(String message) {
        if (!Bot.logged) return;
        message = Util.handleDiscordMention(Strings.stripColors(message));

        JSONObject discordConfig = config.getJSONObject("discord");

        // Check if channel_id is not blank
        if (!discordConfig.getString("channel_id").isBlank()) {
            Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(discordConfig.getString("channel_id"));

            // If the channel exists, send message
            if (optionalChannel.isPresent()) {
                optionalChannel.get().sendMessage(message);
            } else Log.info("[MindustryBR] The channel id is invalid or the channel is unreachable");
        }
    }

    /**
     * @param playerChatEvent Player chat event
     */
    public static void sendMsgToDiscord(PlayerChatEvent playerChatEvent) throws IOException, InterruptedException {
        if (!Bot.logged) return;
        String msg = "**" + Util.handleName(playerChatEvent.player, true, true) + "**: " + playerChatEvent.message;
        msg = Strings.stripColors(msg);

        String msgTmp = Strings.stripColors(playerChatEvent.message);
        if (Util.ip2country(playerChatEvent.player.ip()) != null && !Util.ip2country(playerChatEvent.player.ip()).equalsIgnoreCase("brazil") && !Translate.detect(msgTmp).equalsIgnoreCase("pt")) {
            JSONObject res = new JSONObject(Translate.translate(msgTmp, "pt"));
            if (res.has("translated") && res.getJSONObject("translated").has("text")) {
                String translated = res.getJSONObject("translated").getString("text");
                msg += "\n\n**Traduzido:**```\n" + translated + "\n```";
            }
        }

        sendMsgToDiscord(msg);
    }

    /**
     * @param name    Player name
     * @param message Message
     */
    public static void sendMsgToDiscord(String name, String message) {
        if (!Bot.logged) return;
        String msg = "**" + Util.handleName(name, true, true) + "**: " + message;
        sendMsgToDiscord(msg);
    }

    // Send log message to Discord server

    /**
     * @param message Message
     */
    public static void sendLogMsgToDiscord(String message) {
        if (!Bot.logged) return;
        message = Util.handleDiscordMention(Strings.stripColors(message));

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

    /**
     * @param e Event
     */
    public static void sendLogMsgToDiscord(PlayerChatEvent e) {
        if (!Bot.logged) return;
        String msg = "(" + e.player.getInfo().id + ") **" + e.player.name + "**: " + e.message;
        sendLogMsgToDiscord(msg);
    }

}

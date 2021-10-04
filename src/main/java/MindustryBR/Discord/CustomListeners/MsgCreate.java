package MindustryBR.Discord.CustomListeners;

import MindustryBR.Discord.Commands.*;
import MindustryBR.internal.util.sendMsgToGame;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.stream.Stream;

import static MindustryBR.Main.config;

public class MsgCreate implements MessageCreateListener {
    private final DiscordApi bot;

    public MsgCreate(DiscordApi _bot) {
        bot = _bot;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String prefix = config.getJSONObject("discord").getString("prefix");

        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();

            if (!channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) ||
                    !event.getMessageAuthor().isRegularUser()) return;

            if (!event.getMessageContent().startsWith(prefix)) {
                new sendMsgToGame(bot, event, config);
                return;
            }

            String[] args = Stream.of(event.getMessageContent().split(" ")).filter(str -> !str.isBlank()).toArray(String[]::new);

            switch (args[0].replaceFirst(prefix, "")) {
                case "help" -> new Help(bot, config, event, args);
                case "historico", "history" -> new historyDC(bot, config, event, args);
                case "gameinfo" -> new GameInfo(bot, config, event, args);
                case "ip" -> new ip(bot, config, event, args);
                case "kp", "kickplayer" -> new KickID(bot, config, event, args);
                case "pp", "pardonplayer" -> new PardonID(bot, config, event, args);
                case "bp", "banplayer" -> {
                    try {
                        new BanID(bot, config, event, args);
                    } catch (IOException | GeoIp2Exception e) {
                        e.printStackTrace();
                    }
                }
                case "ubp", "unbanplayer" -> {
                    try {
                        new UnbanID(bot, config, event, args);
                    } catch (IOException | GeoIp2Exception e) {
                        e.printStackTrace();
                    }
                }
                case "pi", "playerinfo" -> {
                    try {
                        new InfoPlayer(bot, config, event, args);
                    } catch (IOException | GeoIp2Exception e) {
                        e.printStackTrace();
                    }
                }
                case "hs", "hoststatus", "status" -> {
                    try {
                        new HostStatus(bot, config, event, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

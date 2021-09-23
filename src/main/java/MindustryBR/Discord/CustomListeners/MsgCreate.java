package MindustryBR.Discord.CustomListeners;

import MindustryBR.Discord.Commands.*;
import MindustryBR.internal.util.sendMsgToGame;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

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
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                new sendMsgToGame(bot, event, config);
            }

            if (!event.getMessageAuthor().isRegularUser() || !event.getMessageContent().startsWith(prefix)) return;

            String[] args = Stream.of(event.getMessageContent().split(" ")).filter(str -> !str.isBlank()).distinct().toArray(String[]::new);

            switch (args[0].replaceFirst(prefix, "")) {
                case "help" -> new Help(bot, config, event, args);
                case "gameinfo" -> new GameInfo(bot, config, event, args);
                case "ip" -> new ip(bot, config, event, args);
                case "bp", "banplayer" -> new BanID(bot, config, event, args);
                case "ubp", "unbanplayer" -> new UnbanID(bot, config, event, args);
                case "kp", "kickplayer" -> new KickID(bot, config, event, args);
                case "pp", "pardonplayer" -> new PardonID(bot, config, event, args);
                case "pi", "playerinfo" -> new InfoPlayer(bot, config, event, args);
            }
        }
    }
}

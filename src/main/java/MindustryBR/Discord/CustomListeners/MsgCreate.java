package MindustryBR.Discord.CustomListeners;

import MindustryBR.Discord.Commands.*;
import MindustryBR.internal.dcRelay.sendMsgToGame;
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
                case "h", "historico", "historia", "history" -> new HistoryDC(bot, config, event, args);
                case "ph", "playerhistory" -> new PlayerHistoryDC(bot, config, event, args);
                case "logiccode", "code", "logic" -> new logicCode(bot, config, event, args);
                case "gameinfo" -> new GameInfo(bot, config, event, args);
                case "ip" -> new ip(bot, config, event, args);
                case "kp", "kickplayer" -> new KickID(bot, config, event, args);
                case "pp", "pardonplayer" -> new PardonID(bot, config, event, args);
                case "bp", "banplayer" -> new BanID(bot, config, event, args);
                case "ubp", "unbanplayer" -> new UnbanID(bot, config, event, args);
                case "pi", "playerinfo" -> {
                    try {
                        new InfoPlayer(bot, config, event, args);
                    } catch (IOException e) {
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
                case "link" -> {
                    channel.sendMessage( event.getMessageAuthor().asUser().get().getMentionTag() + ", esse comando só pode ser usado no DM");
                    event.getMessage().delete();
                }
            }
        } else if (event.isPrivateMessage()) {
            String[] args = Stream.of(event.getMessageContent().split(" ")).filter(str -> !str.isBlank()).toArray(String[]::new);

            switch (args[0].replaceFirst(prefix, "")) {
                case "help" -> new Help(bot, config, event, args);
                case "link" -> new LinkAccount(bot, config, event, args);
            }
        }
    }
}

package MindustryBR.Discord.CustomListeners;

import MindustryBR.Discord.Commands.*;
import MindustryBR.internal.DiscordRelay;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.stream.Stream;

import static MindustryBR.Main.config;

public class MsgCreate implements MessageCreateListener {

    public MsgCreate() {
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String prefix = config.getJSONObject("discord").getString("prefix");

        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();

            if (!channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) ||
                    !event.getMessageAuthor().isRegularUser()) return;

            if (!event.getMessageContent().startsWith(prefix) && !event.getMessageContent().isBlank()) {
                DiscordRelay.sendMsgToGame(event);
                return;
            }

            String[] args = Stream.of(event.getMessageContent().split(" ")).filter(str -> !str.isBlank()).toArray(String[]::new);

            switch (args[0].replaceFirst(prefix, "")) {
                case "help" -> new Help(event, args);
                case "h", "historico", "historia", "history" -> new HistoryDC(event, args);
                case "ph", "playerhistory" -> new PlayerHistoryDC(event, args);
                case "logiccode", "code", "logic" -> new logicCode(event, args);
                case "gameinfo" -> new GameInfo(event, args);
                case "ip" -> new ip(event, args);
                case "kp", "kickplayer" -> new KickID(event, args);
                case "pp", "pardonplayer" -> new PardonID(event, args);
                case "bp", "banplayer" -> new BanID(event, args);
                case "ubp", "unbanplayer" -> new UnbanID(event, args);
                case "pi", "playerinfo" -> {
                    try {
                        new InfoPlayer(event, args);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "hs", "hoststatus", "status" -> {
                    try {
                        new HostStatus(event, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "link" -> {
                    channel.sendMessage(event.getMessageAuthor().asUser().get().getMentionTag() + ", esse comando só pode ser usado no DM");
                    event.getMessage().delete();
                }
                case "config" -> new ConfigServer(event, args);
            }
        } else if (event.isPrivateMessage() && event.getPrivateChannel().isPresent()) {
            String[] args = Stream.of(event.getMessageContent().split(" ")).filter(str -> !str.isBlank()).toArray(String[]::new);

            switch (args[0].replaceFirst(prefix, "")) {
                case "help" -> new Help(event, args);
                case "link" -> new LinkAccount(event, args);
                default ->
                        event.getPrivateChannel().get().sendMessage(event.getMessageAuthor().asUser().get().getMentionTag() + ", esse comando só pode ser usado no servidor");
            }
        }
    }
}

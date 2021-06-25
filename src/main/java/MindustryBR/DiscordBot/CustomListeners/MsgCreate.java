package MindustryBR.DiscordBot.CustomListeners;

import MindustryBR.DiscordBot.Commands.GameInfo;
import MindustryBR.util.sendMsgToGame;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.json.JSONObject;

public class MsgCreate implements MessageCreateListener {
    private final JSONObject config;
    private final DiscordApi bot;

    public MsgCreate(DiscordApi _bot, JSONObject _config) {
        bot = _bot;
        config = _config;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                new sendMsgToGame(bot, event, config);
            }

            if(event.getMessageContent().startsWith("!gameinfo")) {
                new GameInfo(bot, config, event);
            }
        }
    }
}

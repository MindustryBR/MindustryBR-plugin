package MindustryBR.DiscordBot.CustomListeners;

import MindustryBR.util.sendMsgToGame;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.json.JSONObject;

public class MsgCreate implements MessageCreateListener {
    private final JSONObject config;

    public MsgCreate(JSONObject _config) {
        config = _config;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                new sendMsgToGame(event, config);
            }
        }
    }
}

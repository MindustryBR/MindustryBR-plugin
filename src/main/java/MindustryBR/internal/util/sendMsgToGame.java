package MindustryBR.internal.util;

import arc.util.Log;
import mindustry.gen.Call;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

public class sendMsgToGame {
    public sendMsgToGame(DiscordApi bot, MessageCreateEvent event, JSONObject config) {
        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                String name = Util.handleName(event.getMessageAuthor().getDisplayName(), false);
                name = Util.handleDiscordMD(name, true);

                String msg = event.getMessage().getReadableContent().replace("\n", " ");
                msg = Util.handleDiscordMD(msg, true);

                Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
                Log.info("DISCORD > [" + name + "]: " + msg);
            }
        }
    }
}
package MindustryBR.util;

import arc.util.Log;
import mindustry.gen.Call;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

public class sendMsgToGame {
    public sendMsgToGame(MessageCreateEvent event, JSONObject config) {
        if (event.getServerTextChannel().isPresent()) {
            ServerTextChannel channel = event.getServerTextChannel().get();
            if (channel.getIdAsString().equals(config.getJSONObject("discord").getString("channel_id")) && event.getMessageAuthor().isRegularUser()) {
                String name;
                if (event.getMessageAuthor().getDisplayName().toLowerCase().contains("admin") || event.getMessageAuthor().getDisplayName().toLowerCase().contains("adm")) {
                    name = "retardado";
                } else if (event.getMessageAuthor().getDisplayName().toLowerCase().contains("dono")) {
                    name = "retardado²";
                } else {
                    name = event.getMessageAuthor().getDisplayName();
                }

                Call.sendMessage("[orange][[[]" + name + "[orange]]:[] " + event.getMessage().getReadableContent().replace("\n", " "));
                Log.info("DISCORD > [" + event.getMessageAuthor().getDisplayName() + "]: " + event.getMessage().getReadableContent().replace("\n", " "));
            }
        }
    }
}

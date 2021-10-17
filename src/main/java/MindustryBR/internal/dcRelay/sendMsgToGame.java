package MindustryBR.internal.dcRelay;

import MindustryBR.internal.Util;
import arc.util.Log;
import mindustry.gen.Call;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

public class sendMsgToGame {
    public sendMsgToGame(DiscordApi bot, MessageCreateEvent event, JSONObject config) {
        if (event.getServerTextChannel().isPresent()) {
            String name = Util.handleName(event.getMessageAuthor().getDisplayName(), false, true);
            String msg = event.getMessage().getReadableContent().replace("\n", " ");
            msg = Util.handleDiscordMD(msg);

            Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
            Log.info("DISCORD > [" + name + "]: " + msg);
        }
    }

    public sendMsgToGame(DiscordApi bot, MessageAuthor author, String msg, JSONObject config) {
        String name = Util.handleName(author.getDisplayName(), false);
        msg = Util.handleDiscordMD(msg);

        Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
        Log.info("DISCORD > [" + name + "]: " + msg);
    }

    public sendMsgToGame(DiscordApi bot, String author, String msg, JSONObject config) {
        String name = Util.handleName(author, false, true);
        name = Util.handleDiscordMD(name, true);
        msg = Util.handleDiscordMD(msg, true);

        Call.sendMessage("[blue]\uE80D[] [orange][[[]" + name + "[orange]]:[] " + msg);
        Log.info("DISCORD > [" + name + "]: " + msg);
    }
}
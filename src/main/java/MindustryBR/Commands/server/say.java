package MindustryBR.Commands.server;

import MindustryBR.internal.util.sendMsgToDiscord;
import MindustryBR.internal.util.sendLogMsgToDiscord;
import mindustry.gen.Call;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class say {
    public say(DiscordApi bot, JSONObject config, String[] args) {
        StringBuilder msgBuilder = new StringBuilder();

        for (String s : args) {
            msgBuilder.append(s).append(" ");
        }

        String msg = "[white]Server:[] " + msgBuilder.toString();

        Call.sendMessage(msg);
        new sendMsgToDiscord(bot, config, msg);
    }
}

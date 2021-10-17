package MindustryBR.Mindustry.Commands.server;

import MindustryBR.internal.dcRelay.sendMsgToDiscord;
import arc.util.Log;
import arc.util.Strings;
import mindustry.gen.Call;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class say {
    public static void run (DiscordApi bot, JSONObject config, String[] args) {
        StringBuilder msgBuilder = new StringBuilder();

        for (String s : args) {
            msgBuilder.append(s).append(" ");
        }
        String name = "[red][Server][]";
        String msg = msgBuilder.toString();

        Call.sendMessage(name + ": " + msg);
        Log.info(Strings.stripColors(name + ": " + msg));
        new sendMsgToDiscord(bot, config, name, msg);
    }
}

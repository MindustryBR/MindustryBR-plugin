package MindustryBR.Mindustry.Events;

import MindustryBR.internal.dcRelay.sendLogMsgToDiscord;
import MindustryBR.internal.dcRelay.sendMsgToDiscord;
import mindustry.game.EventType.PlayerChatEvent;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.IOException;

public class playerChat {
    public static void run(DiscordApi bot, JSONObject config, PlayerChatEvent e) throws IOException, InterruptedException {
        // Send message to discord
        if (!config.getJSONObject("discord").getString("token").isBlank()) {
            if (e.message.startsWith("/")) {
                new sendLogMsgToDiscord(bot, config, e);
            } else new sendMsgToDiscord(bot, config, e);
        }
    }
}

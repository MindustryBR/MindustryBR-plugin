package MindustryBR.Mindustry.Events;

import MindustryBR.Discord.Bot;
import MindustryBR.internal.DiscordRelay;
import mindustry.game.EventType.PlayerChatEvent;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.IOException;

public class playerChat {
    public static void run(DiscordApi bot, JSONObject config, PlayerChatEvent e) throws IOException, InterruptedException {
        if (bot == null || !Bot.logged) return;
        // Send message to discord
        if (!config.getJSONObject("discord").getString("token").isBlank()) {
            if (e.message.startsWith("/")) {
                DiscordRelay.sendLogMsgToDiscord(e);
            } else DiscordRelay.sendMsgToDiscord(e);
        }
    }
}

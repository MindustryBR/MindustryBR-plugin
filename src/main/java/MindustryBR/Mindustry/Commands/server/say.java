package MindustryBR.Mindustry.Commands.server;

import MindustryBR.internal.DiscordRelay;
import MindustryBR.internal.Classes.Commands.ServerCommand;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.gen.Call;

public class say implements ServerCommand {
    @Nullable
    public static final String params = "<message...>";
    @Nullable
    public static final String desc = "[MindustryBR] Send message as Server";

    public static void run(String[] args) {
        StringBuilder msgBuilder = new StringBuilder();

        for (String s : args) {
            msgBuilder.append(s).append(" ");
        }
        String name = "[red][Server][]";
        String msg = msgBuilder.toString();

        Call.sendMessage(name + ": " + msg);
        Log.info(Strings.stripColors(name + ": " + msg));
        DiscordRelay.sendMsgToDiscord(name, msg);
    }
}

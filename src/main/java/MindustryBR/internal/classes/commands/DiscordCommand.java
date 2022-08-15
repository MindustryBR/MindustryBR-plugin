package MindustryBR.internal.classes.commands;

import arc.util.Nullable;
import org.javacord.api.event.message.MessageCreateEvent;

public interface DiscordCommand {
    public static final boolean adminOnly = false;
    @Nullable
    public static final String params = null;
    @Nullable
    public static final String desc = null;

    public static void run(MessageCreateEvent event, String[] args) {};
}

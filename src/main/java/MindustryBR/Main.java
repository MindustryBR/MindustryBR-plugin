package MindustryBR;

import MindustryBR.Commands.server.say;
import MindustryBR.Discord.Bot;
import MindustryBR.Commands.client.dm;
import MindustryBR.Events.*;
import MindustryBR.internal.util.Util;
import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType.*;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class Main extends Plugin{
    public static JSONObject config = new JSONObject();
    public static DiscordApi bot;

    public Main() {
        Events.on(PlayerJoin.class, e -> playerJoin.run(bot, config, e));
        Events.on(PlayerLeave.class, e -> playerLeave.run(bot, config, e));
        Events.on(PlayerChatEvent.class, e -> playerChat.run(bot, config, e));

        // Testing
        //Events.on(GameOverEvent.class, e -> gameover.run(bot, config, e));
        //Events.on(WaveEvent.class, e-> wave.run(bot, config, e));
    }

    // Called when game initializes
    @Override
    public void init() {
        createConfig();

        // Start the discord bot if token was provided
        if (!config.isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) {
            bot = Bot.run();
        }
    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("reloadconfig", "[MindustryBR] Reload plugin config", args -> this.loadConfig());

        handler.register("saydc", "<message...>", "[MindustryBR] Send message as Server", args -> say.run(bot, config, args));

        handler.register("startbot", "[force]", "[MindustryBR] Start the discord bot if it isnt already online", args -> {
            // Start the discord bot if token was provided and the bot isnt online
            if (((!config.isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) || args.length > 0) && !Bot.logged) {
                if (args.length > 0) Log.info("force starting bot");
                bot = Bot.run();
            }
        });
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("dm", "<player> <message...>", "Mande uma mensagem privada para um jogador.", (args, player) -> dm.run(bot , config, args, player));

        // handler.<Player>register("name", "params", "description", (args, player) -> { /* code here */ });
    }

    private void createConfig() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").exists()) {
            loadConfig();
            return;
        }

        // Make default config
        JSONObject defaultConfig = new JSONObject();
        JSONObject defaultPrefix = new JSONObject();
        JSONObject defaultDiscordConfig = new JSONObject();
        JSONObject defaultResourceEmoji = new JSONObject();

        defaultConfig.put("owner_id", "");
        defaultConfig.put("version", 1);

        defaultPrefix.put("owner_prefix", "[sky][Dono][] %1");
        defaultPrefix.put("admin_prefix", "[blue][Admin][] %1");
        defaultPrefix.put("user_prefix", "%1");

        String[] discordStrings = {
                "token",
                "channel_id",
                "log_channel_id",
                "serverstatus_channel_id",
                "admin_role_id",
                "owner_role_id",
                "mod_role_id"
        };

        for (String ds : discordStrings) {
            defaultDiscordConfig.put(ds, "");
        }

        defaultDiscordConfig.put("prefix", "!");

        for (String rn : Util.resourcesRawName) {
            defaultResourceEmoji.put(rn, "");
        }

        defaultDiscordConfig.put("emojis", defaultResourceEmoji);
        defaultConfig.put("discord", defaultDiscordConfig);
        defaultConfig.put("prefix", defaultPrefix);

        // Create config file
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString(4));
        config = defaultConfig;
    }

    private void loadConfig() {
        config = new JSONObject(this.getConfig().readString());
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(config.toString(4));
        Log.info("[MindustryBR] Config loaded");
    }
}

package MindustryBR;

import MindustryBR.DiscordBot.Bot;
import MindustryBR.Events.playerJoin;
import MindustryBR.Events.playerLeave;
import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType.PlayerChatEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

public class MindustryBR extends Plugin{
    private static JSONObject config = new JSONObject();
    private static DiscordApi bot;

    public MindustryBR() {
        Events.on(PlayerJoin.class, e -> { playerJoin.run(bot, config, e); });
        Events.on(PlayerLeave.class, e -> { playerLeave.run(bot, config, e); });

        Events.on(PlayerChatEvent.class, e -> {
            // Send message to discord
            if (!config.getJSONObject("discord").getString("token").isBlank()) {
                if (e.message.startsWith("/")) {
                    new sendLogMsgToDiscord(bot, config, e);
                } else new sendMsgToDiscord(bot, config, e);
            }
        });
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
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){
        // Register a DM command
        handler.<Player>register("dm", "<player> <texto...>", "Mande uma mensagem privada para um jogador.", (args, player) -> {
            // Find player by name
            Player other = Groups.player.find(p -> p.name.toLowerCase().contains(args[0].toLowerCase()));

            // Give error message if player isn't found
            if(other == null){
                player.sendMessage("[scarlet]Nenhum jogador encontrado com esse nome!");
                return;
            }

            // Send the other player a private message
            other.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
            // Send a message to the player that used the command
            player.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
        });
    }

    private void createConfig() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").exists()) {
            loadConfig();
            return;
        }

        // Make default config
        JSONObject defaultConfig = new JSONObject();
        defaultConfig.put("owner_id", "");
        defaultConfig.put("version", 1);

        JSONObject defaultPrefix = new JSONObject();
        defaultPrefix.put("owner_prefix", "[sky][Dono][] %1");
        defaultPrefix.put("admin_prefix", "[blue][Admin][] %1");
        defaultPrefix.put("user_prefix", "%1");

        JSONObject defaultDiscordConfig = new JSONObject();
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

        defaultConfig.put("discord", defaultDiscordConfig);

        // Create config file
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString(4));
        config = defaultConfig;
    }

    /**
     * Load config
     */
    private void loadConfig() {
        config = new JSONObject(this.getConfig().readString());
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(config.toString(4));
        Log.info("[MindustryBR] Config loaded");
    }
}

package MindustryBR;

import MindustryBR.Commands.client.dm;
import MindustryBR.Commands.client.history;
import MindustryBR.Commands.server.say;
import MindustryBR.Discord.Bot;
import MindustryBR.Events.*;
import MindustryBR.internal.classes.history.LimitedQueue;
import MindustryBR.internal.classes.history.entry.BaseEntry;
import arc.Core;
import arc.Events;
import arc.util.CommandHandler;
import arc.util.Log;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import mindustry.game.EventType.*;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import org.javacord.api.DiscordApi;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Plugin {
    public static JSONObject config = new JSONObject();
    public static JSONObject resources = new JSONObject();
    public static DiscordApi bot;
    public static DatabaseReader dbReader = null;
    public static LimitedQueue<BaseEntry>[][] worldHistory;
    public static ArrayList<Player> activeHistoryPlayers = new ArrayList<>();

    public Main() throws IOException {
        // GeoLite2 database initializer
        dbReader = new DatabaseReader.Builder(Core.settings.getDataDirectory().child("mods/MindustryBR/GeoLite2-Country.mmdb").file()).build();

        // Misc events
        Events.on(ConfigEvent.class, e -> configEvent.run(bot, config, e));
        Events.on(TapEvent.class, e -> tap.run(bot, config, e));

        // Game events
        Events.on(GameOverEvent.class, e -> gameover.run(bot, config, e));
        Events.on(WaveEvent.class, e -> wave.run(bot, config, e));
        Events.on(WorldLoadEvent.class, e -> worldLoad.run(bot, config, e));

        // Build events
        Events.on(BlockBuildBeginEvent.class, e -> blockBuildBegin.run(bot, config, e));
        Events.on(BlockBuildEndEvent.class, e -> blockBuildEnd.run(bot, config, e));
        Events.on(BlockDestroyEvent.class, e -> blockDestroy.run(bot, config, e));

        // Units events
        Events.on(UnitCreateEvent.class, e -> unitCreate.run(bot, config, e));
        Events.on(UnitDestroyEvent.class, e -> unitDestroy.run(bot, config, e));
        Events.on(UnitDrownEvent.class, e -> unitDrown.run(bot, config, e));

        // Players event
        Events.on(PlayerJoin.class, e -> playerJoin.run(bot, config, e));
        Events.on(PlayerLeave.class, e -> playerLeave.run(bot, config, e));
        Events.on(PlayerChatEvent.class, e -> playerChat.run(bot, config, e));
        Events.on(PlayerBanEvent.class, e -> {
            try {
                playerBan.run(bot, config, e);
            } catch (IOException | GeoIp2Exception ex) {
                ex.printStackTrace();
            }
        });
        Events.on(PlayerUnbanEvent.class, e -> {
            try {
                playerUnban.run(bot, config, e);
            } catch (IOException | GeoIp2Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    // Called when the server initializes
    @Override
    public void init() {
        createConfig();
        createResources();

        // Start the discord bot if token was provided
        if (!config.isEmpty() && !config.getJSONObject("discord").isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) {
            bot = Bot.run();
        }
    }

    // Register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("reloadconfig", "[MindustryBR] Reload plugin config", args -> {
            this.createConfig();
            this.createResources();
        });

        handler.register("saydc", "<message...>", "[MindustryBR] Send message as Server", args -> say.run(bot, config, args));

        handler.register("startbot", "[force]", "[MindustryBR] Start the discord bot if it isn't already online", args -> {
            // Start the discord bot if token was provided and the bot isn't online
            if (((!config.isEmpty() && !config.getJSONObject("discord").getString("token").isBlank()) || args.length > 0) && !Bot.logged) {
                if (args.length > 0) Log.info("force starting bot");
                bot = Bot.run();
            }
        });
    }

    // Register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("dm", "<player> <message...>", "Mande uma mensagem privada para um jogador.", (args, player) -> dm.run(bot , config, args, player));

        handler.<Player>register("history", "Ative o historico do bloco", (args, player) -> history.run(bot, config, args, player));

        // handler.<Player>register("name", "params", "description", (args, player) -> { /* code here */ });
    }

    private void createResources() {
        // Load config file if it already exists
        if (Core.settings.getDataDirectory().child("mods/MindustryBR/resources.json").exists()) {
            loadResources();
            return;
        }

        JSONObject defaultResources = new JSONObject("{\"blast-compound\": \"Composto de explosao\", \"coal\": \"Carvao\", \"copper\": \"Cobre\", \"graphite\": \"Grafite\", \"lead\": \"Chumbo\", \"metaglass\": \"Metavidro\", \"phase-fabric\": \"Tecido de fase\", \"plastanium\": \"Plastanio\", \"pyratite\": \"Piratita\", \"sand\": \"Areia\", \"scrap\": \"Sucata\", \"silicon\": \"Silicio\", \"spore-pod\": \"Capsula de esporos\", \"surge-alloy\": \"Liga de surto\", \"thorium\": \"Torio\", \"titanium\": \"Titanio\"}");

        Core.settings.getDataDirectory().child("mods/MindustryBR/resources.json").writeString(defaultResources.toString(4));
        resources = defaultResources;
    }

    private void loadResources() {
        resources = new JSONObject(Core.settings.getDataDirectory().child("mods/MindustryBR/resources.json").readString());
        Log.info("[MindustryBR] Resources loaded");
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

        defaultConfig.put("owner_id", "");
        defaultConfig.put("version", 1);
        defaultConfig.put("ip", "");

        defaultPrefix.put("owner_prefix", "[sky][Dono][] %1");
        defaultPrefix.put("admin_prefix", "[blue][Admin][] %1");
        defaultPrefix.put("user_prefix", "%1");

        String[] discordStrings = {
                "token",
                "channel_id",
                "log_channel_id",
                "mod_channel_id",
                "serverstatus_channel_id",
                "admin_role_id",
                "owner_role_id",
                "mod_role_id",
                "emoji-bank"
        };

        for (String ds : discordStrings) {
            defaultDiscordConfig.put(ds, "");
        }

        defaultDiscordConfig.put("prefix", "!");

        defaultConfig.put("discord", defaultDiscordConfig);
        defaultConfig.put("prefix", defaultPrefix);
        defaultConfig.put("name", "Survival");

        // Create config file
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString(4));
        config = defaultConfig;
    }

    private void loadConfig() {
        config = new JSONObject(this.getConfig().readString());
        Log.info("[MindustryBR] Config loaded");
    }
}

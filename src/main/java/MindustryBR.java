import DiscordBot.Main;
import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.Plugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Optional;

import static mindustry.Vars.state;


public class MindustryBR extends Plugin{
    public JSONObject config;
    private DiscordApi bot;

    public MindustryBR() {
        Events.on(PlayerJoin.class, e -> {
            // Check for non-admin players with admin in name
            if (!e.player.admin) {
                if (e.player.name.toLowerCase().contains("admin") || e.player.name.toLowerCase().contains("adm")) {
                    e.player.name = "retardado";
                } else if (e.player.name.toLowerCase().contains("dono")) {
                    e.player.name = "retardado²";
                }
            }

            // Rename players to use the tag system
            JSONObject prefix = this.config.getJSONObject("prefix");

            if (e.player.getInfo().id.equals(this.config.getString("owner_id"))) {
                e.player.name = prefix.getString("owner_prefix").replace("%1", e.player.name);
            } else if (e.player.admin) {
                e.player.name = prefix.getString("admin_prefix").replace("%1", e.player.name);
            } else {
                e.player.name = prefix.getString("user_prefix").replace("%1", e.player.name);
            }


            // Unpause the game if one or more player is connected
            if (Groups.player.size() >= 1 && state.serverPaused) {
                state.serverPaused = false;
                Log.info("auto-pause: " + Groups.player.size() + " jogador conectado -> Jogo despausado...");
                Call.sendMessage("[scarlet][Server][]: Jogo despausado...");
            }

            // Send connect message to discord
            if (!this.config.getJSONObject("discord").getString("token").isBlank()) {
                Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("channel_id"));
                Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("log_channel_id"));

                String msg = ":inbox_tray: " + Strings.stripColors(e.player.name) + " conectou";

                if (optionalChannel.isPresent()) {
                    ServerTextChannel channel = optionalChannel.get();

                    channel.sendMessage(msg);
                } else {
                    Log.info("[MindustryBR] The channel id provided is invalid or the channel is unreachable");
                }

                if (optionalLogChannel.isPresent()) {
                    ServerTextChannel logChannel = optionalLogChannel.get();

                    logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
                } else {
                    Log.info("[MindustryBR] The log channel id provided is invalid or the channel is unreachable");
                }
            }
        });

        Events.on(PlayerLeave.class, e -> {
            // Pause the game if no one is connected
            if (Groups.player.size()-1 < 1) {
                state.serverPaused = true;
                Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
            }


            // Send disconnect message to discord
            if (!this.config.getJSONObject("discord").getString("token").isBlank()) {
                Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("channel_id"));
                Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("log_channel_id"));

                String msg = ":outbox_tray: " + Strings.stripColors(e.player.name) + " desconectou";

                if (optionalChannel.isPresent()) {
                    ServerTextChannel channel = optionalChannel.get();

                    channel.sendMessage(msg);
                } else {
                    Log.info("[MindustryBR] The channel id provided is invalid or the channel is unreachable");
                }

                if (optionalLogChannel.isPresent()) {
                    ServerTextChannel logChannel = optionalLogChannel.get();

                    logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
                } else {
                    Log.info("[MindustryBR] The log channel id provided is invalid or the channel is unreachable");
                }
            }
        });

        Events.on(PlayerChatEvent.class, e -> {
            // Send message to discord
            if (!this.config.getJSONObject("discord").getString("token").isBlank()) {
                Optional<ServerTextChannel> optionalChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("channel_id"));
                Optional<ServerTextChannel> optionalLogChannel = bot.getServerTextChannelById(this.config.getJSONObject("discord").getString("log_channel_id"));


                String msg = "**" + Strings.stripColors(e.player.name) + "**: " + e.message;

                if (optionalChannel.isPresent() && !e.message.startsWith("/")) {
                    ServerTextChannel channel = optionalChannel.get();

                    channel.sendMessage(msg);
                } else {
                    Log.info("[MindustryBR] The channel id provided is invalid or the channel is unreachable");
                }

                if (optionalLogChannel.isPresent()) {
                    ServerTextChannel logChannel = optionalLogChannel.get();

                    logChannel.sendMessage("[" + LocalDateTime.now().toString().substring(0, 19) + "] " + msg);
                } else {
                    Log.info("[MindustryBR] The log channel id provided is invalid or the channel is unreachable");
                }
            }
        });
    }

    // Called when game initializes
    @Override
    public void init() {
        // Create config file if it doesn't exist
        if (!Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").exists()) {
            JSONObject defaultConfig = new JSONObject();
            defaultConfig.put("owner_id", "");

            JSONObject defaultPrefix = new JSONObject();
            defaultPrefix.put("owner_prefix", "[sky][Dono][] %1");
            defaultPrefix.put("admin_prefix", "[blue][Admin][] %1");
            defaultPrefix.put("user_prefix", "%1");

            defaultConfig.put("prefix", defaultPrefix);

            JSONObject defaultDiscordConfig = new JSONObject();
            String[] discordStrings = {
                    "token",
                    "channel_id",
                    "log_channel_id",
                    "serverstatus_channel_id",
                    "admin_role_id",
                    "mod_role_id"
            };

            for (String ds : discordStrings) {
                defaultDiscordConfig.put(ds, "");
            }

            defaultDiscordConfig.put("prefix", "!");

            defaultConfig.put("discord", defaultDiscordConfig);

            Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString(4));
            this.config = defaultConfig;
        } else {
            this.config = new JSONObject(this.getConfig().readString());
        }

        // Start the discord bot if token was provided
        if (!this.config.isEmpty() && !this.config.getJSONObject("discord").getString("token").isBlank()) {
            this.bot = Main.run(this.config);
        }
    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("reloadconfig", "[MindustryBR] Reload plugin config", args -> {
           this.reloadConfig();
        });

        // Start discord bot
        handler.register("startbot", "[MindustryBR] Start bot", args -> {
            this.bot = Main.run(this.config);
        });
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){
        //register a whisper command which can be used to send other players messages
        handler.<Player>register("dm", "<player> <texto...>", "Mande uma mensagem privada para um jogador.", (args, player) -> {
            //find player by name
            Player other = Groups.player.find(p -> p.name.contains(args[0]));

            //give error message with scarlet-colored text if player isn't found
            if(other == null){
                player.sendMessage("[scarlet]Nenhum jogador encontrado com esse nome!");
                return;
            }

            //send the other player a message, using [lightgray] for gray text color and [] to reset color
            other.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
            player.sendMessage("[lightgray](DM)[] " + player.name + "[white]:[] " + args[1]);
        });
    }

    private void reloadConfig() {
        this.config = new JSONObject(this.getConfig().readString());
        Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(this.config.toString(4));
        Log.info("[MindustryBR] Config reloaded");
    }
}

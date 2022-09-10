package MindustryBR.Discord.Commands;

import arc.util.Log;
import mindustry.gen.Call;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static MindustryBR.Main.config;
import static MindustryBR.Main.reloadConfig;
import static mindustry.Vars.state;

public class ConfigServer {
    public ConfigServer(MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();
        MessageBuilder message = new MessageBuilder();

        String owner_role = config.getJSONObject("discord").getString("owner_role_id");

        AtomicBoolean tem = new AtomicBoolean(false);

        event.getMessageAuthor().asUser().get().getRoles(event.getServer().get()).forEach(r -> {
            if (r.getIdAsString().equalsIgnoreCase(owner_role))
                tem.set(true);
        });

        if (!tem.get()) {
            message.append("Somente o dono pode usar esse comando")
                    .send(channel)
                    .join();
            return;
        }

        if (args.length <= 1) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestampToNow()
                    .setColor(Color.GREEN)
                    .setTitle("Lista de configurações")
                    .setDescription("""
                            reload - Recarrega as configurações
                            pause - Pausa/despausa o servidor
                            set <key> <value> - Altera a configuração <key> para <value>
                            get <key> - Exibe a configuração <key>
                            """);

            message.setEmbed(embed)
                    .send(channel)
                    .join();
        } else {
            if (args.length >= 3 && args[2].contains("token")) {
                message.append("Proibido alterar/ver o token do bot")
                        .send(channel)
                        .join();
                return;
            }

            switch (args[1]) {
                case "reload" -> {
                    reloadConfig();
                    message.append("Configurações recarregadas")
                            .send(channel)
                            .join();
                }
                case "pause" -> {
                    state.serverPaused = !state.serverPaused;
                    message.append("Server " + (state.serverPaused ? "pausado" : "despausado"))
                            .send(channel)
                            .join();
                    Call.sendMessage("[scarlet][Server][]: Server " + (state.serverPaused ? "pausado" : "despausado"));
                    Log.info("[MindustryBR] Server " + (state.serverPaused ? "paused" : "unpaused"));
                }
                case "set" -> {
                    String[] arg1 = args[2].split("\\.");

                    if (!config.has(arg1[0])) {
                        StringBuilder tmp = new StringBuilder();

                        config.keys().forEachRemaining(key -> {
                            if (config.get(key).getClass() == JSONObject.class) {
                                JSONObject tmp2 = (JSONObject) config.get(key);

                                tmp2.keys().forEachRemaining(key2 -> {
                                    tmp.append(key).append(".").append(key2).append("\n");
                                });
                            } else {
                                tmp.append(key).append("\n");
                            }
                        });

                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.RED)
                                        .setTitle("Configuração não encontrada")
                                        .setDescription("Lista de configurações:\n" + tmp))
                                .send(channel)
                                .join();
                    } else if (config.get(arg1[0]).getClass() == JSONObject.class) {
                        Object tmpConfig = config.getJSONObject(arg1[0]).get(arg1[1]);
                        config.getJSONObject(arg1[0]).put(arg1[1], args[3]);

                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + arg1[0] + "." + arg1[1] + "`")
                                        .setDescription(arg1[0] + "." + arg1[1] + " alterada para " + args[2] +
                                                "\nEra: " + tmpConfig))
                                .send(channel)
                                .join();

                    } else if (config.get(args[2]).getClass() == JSONArray.class) {
                        JSONArray tmpConfig = config.getJSONArray(args[2]);
                        JSONArray newConfig = new JSONArray();

                        for (int i = 3; i < args.length; i++) {
                            newConfig.put(args[i]);
                        }

                        config.put(args[2], newConfig);

                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + args[2] + "`")
                                        .setDescription(args[2] + " alterada para " + newConfig +
                                                "\nEra: " + tmpConfig))
                                .send(channel)
                                .join();
                    } else {
                        Object tmpConfig = config.get(args[2]);
                        config.put(args[2], args[3]);

                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + args[2] + "`")
                                        .setDescription("Alterada para: " + args[3] +
                                                "\nEra: " + tmpConfig))
                                .send(channel)
                                .join();
                    }
                }
                case "get" -> {
                    String[] arg1 = args.length > 2 ? args[2].split("\\.") : null;

                    if (arg1 == null || !config.has(arg1[0])) {
                        StringBuilder tmp = new StringBuilder();

                        config.keys().forEachRemaining(key -> {
                            if (config.get(key).getClass() == JSONObject.class) {
                                JSONObject tmp2 = (JSONObject) config.get(key);

                                tmp2.keys().forEachRemaining(key2 -> {
                                    tmp.append(key).append(".").append(key2).append("\n");
                                });
                            } else {
                                tmp.append(key).append("\n");
                            }
                        });

                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.RED)
                                        .setTitle("Configuração")
                                        .setDescription(tmp.toString()))
                                .send(channel)
                                .join();
                    } else if (arg1.length == 1 && config.get(arg1[0]).getClass() != JSONObject.class && config.get(arg1[0]).getClass() != JSONArray.class) {
                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + arg1[0] + "`")
                                        .setDescription("`" + config.get(args[2]) + "`"))
                                .send(channel)
                                .join();
                    } else if (config.get(arg1[0]).getClass() == JSONObject.class) {
                        Object tmpConfig = config.getJSONObject(arg1[0]).get(arg1[1]);
                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + arg1[0] + "." + arg1[1] + "`")
                                        .setDescription("`" + tmpConfig + "`"))
                                .send(channel)
                                .join();
                    } else {
                        message.setEmbed(new EmbedBuilder()
                                        .setTimestampToNow()
                                        .setColor(Color.GREEN)
                                        .setTitle("Configuração `" + arg1[0] + "`")
                                        .setDescription("`" + config.getJSONArray(args[2]).join(" , ") + "`"))
                                .send(channel)
                                .join();
                    }
                }
            }
        }

    }
}

package MindustryBR.Discord.Commands;

import MindustryBR.internal.dcRelay.sendLogMsgToDiscord;
import MindustryBR.internal.dcRelay.sendMsgToDiscord;
import MindustryBR.internal.dcRelay.sendMsgToGame;
import mindustry.core.GameState;
import mindustry.net.Administration;
import mindustry.server.ServerControl;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

public class PardonID {
    public PardonID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        if (args.length < 2) {
            new MessageBuilder()
                    .append("Você nao informou o ID do jogador")
                    .send(channel)
                    .join();
            return;
        }

        String mod_role = config.getJSONObject("discord").getString("mod_role_id");
        String adm_role = config.getJSONObject("discord").getString("admin_role_id");
        String owner_role = config.getJSONObject("discord").getString("owner_role_id");

        AtomicBoolean tem = new AtomicBoolean(false);

        event.getMessageAuthor().asUser().get().getRoles(event.getServer().get()).forEach(r -> {
            if (r.getIdAsString().equalsIgnoreCase(mod_role) || r.getIdAsString().equalsIgnoreCase(adm_role) || r.getIdAsString().equalsIgnoreCase(owner_role))
                tem.set(true);
        });

        if (!tem.get()) {
            new MessageBuilder()
                    .append("Voce nao tem permissao para usar esse comando")
                    .send(channel)
                    .join();
            return;
        }

        if (!state.is(GameState.State.playing)) {
            new MessageBuilder()
                    .append("Server nem ta aberto ainda precoce fdp")
                    .send(channel)
                    .join();
            return;
        }

        boolean playerExists = false;
        Administration.PlayerInfo player = null;

        switch (args[1].toLowerCase()) {
            case "name" -> {
                if (netServer.admins.findByName(args[2]).size > 0) {
                    playerExists = true;
                    player = netServer.admins.findByName(args[2]).first();
                    args[2] = player.id;
                }
            }
            case "id" -> {
                if (netServer.admins.getInfoOptional(args[2]) != null) {
                    playerExists = true;
                    player = netServer.admins.getInfoOptional(args[2]);
                    args[2] = player.id;
                }
            }
            case "ip" -> {
                if (netServer.admins.findByIP(args[2]) != null) {
                    playerExists = true;
                    player = netServer.admins.findByIP(args[2]);
                    args[2] = player.id;
                }
            }
            default -> {
                new MessageBuilder()
                        .append("Tipo invalido. Use: id, ip, name")
                        .send(channel)
                        .join();
                return;
            }
        }

        if (!playerExists) {
            new MessageBuilder()
                    .append("Nao achei nenhum jogador com esse nome ou ID")
                    .send(channel)
                    .join();
            return;
        }

        arc.Core.app.getListeners().each(lst -> {
            if (lst instanceof ServerControl) {
                ServerControl scont = (ServerControl) lst;
                System.out.println("pardon " + args[2]);
                scont.handler.handleMessage("pardon " + args[2]);
            }
        });

        new sendMsgToGame(bot, "[red][Server][]", player.lastName + " teve o kick perdoado", config);
        new sendMsgToDiscord(bot, config, "**" + player.lastName + "** (" + player.id + ") teve o kick perdoado");
        new sendLogMsgToDiscord(bot, config, "**" + player.lastName + "** (" + player.id + ") teve o kick perdoado");
    }
}

package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import MindustryBR.internal.util.sendMsgToGame;
import mindustry.core.GameState;
import mindustry.net.Administration;
import mindustry.server.ServerControl;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

public class UnbanID {
    public UnbanID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
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
            if (r.getIdAsString().equalsIgnoreCase(mod_role) || r.getIdAsString().equalsIgnoreCase(adm_role) || r.getIdAsString().equalsIgnoreCase(owner_role)) tem.set(true);
        });

        if (!tem.get()) {
            new MessageBuilder()
                    .append("Voce nao tem permissao para usar esse comando")
                    .send(channel)
                    .join();
            return;
        }

        if(!state.is(GameState.State.playing)) {
            new MessageBuilder()
                    .append("Server nem ta aberto ainda precoce fdp")
                    .send(channel)
                    .join();
            return;
        }


        AtomicBoolean playerExists = new AtomicBoolean(false);
        AtomicReference<Administration.PlayerInfo> player = new AtomicReference<>();

        switch (args[1].toLowerCase()) {
            case "name" -> netServer.admins.getBanned().contains(b -> {
                if (b.lastName.equals(args[2]) || b.names.contains(args[2])) {
                    playerExists.set(true);
                    player.set(b);
                    args[2] = player.get().id;
                }
                return false;
            });
            case "id" -> netServer.admins.getBanned().contains(b -> {
                if (b.id.equals(args[2])) {
                    playerExists.set(true);
                    player.set(b);
                    args[2] = player.get().id;
                }
                return false;
            });
            case "ip" -> netServer.admins.getBanned().contains(b -> {
                if (b.lastIP.equals(args[2])) {
                    playerExists.set(true);
                    player.set(b);
                    args[2] = player.get().lastIP;
                }
                return false;
            });
            default -> {
                new MessageBuilder()
                        .append("Tipo invalido. Use: id, ip, name")
                        .send(channel)
                        .join();
                return;
            }
        }

        if (!playerExists.get()) {
            new MessageBuilder()
                    .append("Nao achei nenhum jogador com esse nome ou ID")
                    .send(channel)
                    .join();
            return;
        }

        arc.Core.app.getListeners().each(lst -> {
            if (lst instanceof ServerControl) {
                ServerControl scont = (ServerControl) lst;
                System.out.println("unban " + args[2]);
                scont.handler.handleMessage("unban " + args[2]);
            }
        });

        new MessageBuilder()
                .append("Jogador **" + player.get().lastName + "** (`" + player.get().id + "`) foi desbanido")
                .send(channel)
                .join();

        new sendMsgToGame(bot, "[red][Server][]", "Jogador " + player.get().lastName + " foi desbanindo", config);
        new sendLogMsgToDiscord(bot, config, "Jogador **" + player.get().lastName + "** (`" + player.get().id + "`) foi banido");
    }
}


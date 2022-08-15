package MindustryBR.Discord.Commands;

import MindustryBR.internal.DiscordRelay;
import arc.util.Strings;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.server.ServerControl;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static MindustryBR.Main.config;
import static mindustry.Vars.state;

public class KickID {
    public KickID(MessageCreateEvent event, String[] args) {
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

        AtomicBoolean playerExists = new AtomicBoolean(false);
        AtomicReference<Player> player = new AtomicReference<>();

        switch (args[1].toLowerCase()) {
            case "name" -> Groups.player.contains(p -> {
                if (p.name.toLowerCase().contains(args[2]) || Strings.stripColors(p.name).contains(args[2])) {
                    player.set(p);
                    playerExists.set(true);
                    return true;
                }
                return false;
            });
            case "id" -> Groups.player.contains(p -> {
                if (p.getInfo().id.equals(args[2])) {
                    player.set(p);
                    playerExists.set(true);
                    return true;
                }
                return false;
            });
            case "ip" -> Groups.player.contains(p -> {
                if (p.getInfo().lastIP.equals(args[2])) {
                    player.set(p);
                    playerExists.set(true);
                    return true;
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
                System.out.println("kick " + player.get().name);
                scont.handler.handleMessage("kick " + player.get().name);
            }
        });

        DiscordRelay.sendMsgToGame("[red][Server][]", player.get().name + " foi kikado");
        DiscordRelay.sendMsgToDiscord("**" + player.get().name + "** (" + player.get().getInfo().id + ") foi kikado");
        DiscordRelay.sendLogMsgToDiscord("**" + player.get().name + "** (" + player.get().getInfo().id + ") foi kikado");
    }
}
package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.sendLogMsgToDiscord;
import MindustryBR.internal.util.sendMsgToDiscord;
import MindustryBR.internal.util.sendMsgToGame;
import mindustry.core.GameState;
import mindustry.net.Administration;
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

        Administration.PlayerInfo info = netServer.admins.getInfoOptional(args[1]);

        if(info != null){
            info.lastKicked = 0;

            new sendMsgToGame(bot, "[red][Server][]", info.lastName + " teve o kick perdoado", config);
            new sendMsgToDiscord(bot, config, "**" + info.lastName + "** (" + info.id + ") teve o kick perdoado");
            new sendLogMsgToDiscord(bot, config, "**" + info.lastName + "** (" + info.id + ") teve o kick perdoado");
        } else {
            new MessageBuilder()
                    .append("Nao achei ninguem com esse ID")
                    .send(channel)
                    .join();
        }
    }
}

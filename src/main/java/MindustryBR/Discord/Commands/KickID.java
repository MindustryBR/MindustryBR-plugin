package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.*;
import arc.util.Strings;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Packets;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.state;

public class KickID {
    public KickID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        if (args.length < 2) {
            new MessageBuilder()
                    .append("Você não informou o ID do jogador")
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
                    .append("Você não tem permissão para usar esse comando")
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

        Player target = Groups.player.find(p -> Strings.stripColors(p.name()).equalsIgnoreCase(args[1]));

        if(target != null){
            target.kick(Packets.KickReason.kick);

            new sendMsgToGame(bot, "[red][Server][]", target.name() + " foi kickado do servidor", config);
            new sendMsgToDiscord(bot, config, target.name() + " foi kickado do servidor");
            new sendLogMsgToDiscord(bot, config, target.name() + " foi kickado do servidor");
        } else{
            new MessageBuilder()
                    .append("Não achei ninguem com esse nome")
                    .send(channel)
                    .join();
        }
    }
}
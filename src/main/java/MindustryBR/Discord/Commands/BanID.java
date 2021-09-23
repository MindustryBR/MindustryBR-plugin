package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.*;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.core.GameState;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.net.Packets;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.netServer;
import static mindustry.Vars.state;

public class BanID {
    public BanID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
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

        Optional<ServerTextChannel> c1 = bot.getServerTextChannelById(config.getJSONObject("discord").getString("mod_channel_id"));
        if (c1.isEmpty()) return;
        ServerTextChannel c2 = c1.get();

        Player target = Groups.player.find(p -> Strings.stripColors(p.name()).equalsIgnoreCase(args[1]));

        if(target != null){
            netServer.admins.banPlayer(target.getInfo().id);
            target.kick(Packets.KickReason.banned);

            new sendMsgToGame(bot, "[red][Server][]", target.name() + " foi banido do servidor", config);
            new sendMsgToDiscord(bot, config, "**" + target.name() + "**" + target.getInfo().id + ") foi banido do servidor");
            new sendLogMsgToDiscord(bot, config, "**" + target.name() + "**" + target.getInfo().id + ") foi banido do servidor");

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(event.getMessageAuthor().asUser().get())
                    .setTitle(target.name() + " foi banido")
                    .setDescription("UUID: " + target.getInfo().id + "\n" +
                            "Nomes usados: " + target.getInfo().names.toString(", ") + "\n" +
                            "Entrou " + target.getInfo().timesJoined + " vez(es)\n" +
                            "Kickado " + target.getInfo().timesKicked + " vez(es)\n")
                    .setTimestampToNow();

            new MessageBuilder()
                    .setEmbed(embed)
                    .send(c2)
                    .join();
        } else if (netServer.admins.banPlayer(args[1])) {
            Seq<Administration.PlayerInfo> bans = netServer.admins.getBanned();
            Administration.PlayerInfo bannedPlayer = null;

            for(Administration.PlayerInfo banned : bans) {
                if (banned.id.equals(args[1])) bannedPlayer = banned;
            }

            new sendMsgToGame(bot, "[red][Server][]", bannedPlayer.lastName + " foi banido do servidor", config);
            new sendMsgToDiscord(bot, config, "**" + bannedPlayer.lastName + "** (" + bannedPlayer.id + ") foi banido do servidor");
            new sendLogMsgToDiscord(bot, config, "**" + bannedPlayer.lastName + "** (" + bannedPlayer.id + ") foi banido do servidor");

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(event.getMessageAuthor().asUser().get())
                    .setTitle(bannedPlayer.lastName + " foi banido")
                    .setDescription("UUID: " + bannedPlayer.id + "\n" +
                            "Nomes usados: " + bannedPlayer.names.toString(", ") + "\n" +
                            "Entrou " + bannedPlayer.timesJoined + " vez(es)\n" +
                            "Kickado " + bannedPlayer.timesKicked + " vez(es)\n")
                    .setTimestampToNow();

            new MessageBuilder()
                    .setEmbed(embed)
                    .send(c2)
                    .join();
        } else {
            new MessageBuilder()
                    .append("Nao achei esse jogador")
                    .send(channel)
                    .join();
        }
    }
}

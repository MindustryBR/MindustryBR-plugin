package MindustryBR.Discord.Commands;

import MindustryBR.internal.util.*;
import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.netServer;

public class BanID {
    public BanID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
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

        Optional<ServerTextChannel> c1 = bot.getServerTextChannelById(config.getJSONObject("discord").getString("mod_channel_id"));
        if (c1.isEmpty()) return;
        ServerTextChannel c2 = c1.get();

        Player target = Groups.player.find(p -> Strings.stripColors(p.name()).equalsIgnoreCase(args[1]));

        if(target != null){
            netServer.admins.banPlayer(target.uuid());

            new sendMsgToGame(bot, "[red][Server][]", target.name() + " foi banido do servidor", config);
            new sendMsgToDiscord(bot, config, target.name() + " foi banido do servidor");
            new sendLogMsgToDiscord(bot, config, target.name() + " foi banido do servidor");

            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(event.getMessageAuthor().asUser().get())
                    .setTitle(target.name() + " foi banido")
                    .setDescription("UUID: " + target.uuid() + "\n" +
                            "Nomes usados: " + target.getInfo().names.toString(", ") + "\n" +
                            "Entrou " + target.getInfo().timesJoined + " vezes\n" +
                            "Kickado " + target.getInfo().timesKicked + " vezes\n")
                    .setTimestampToNow();

            new MessageBuilder()
                    .setEmbed(embed)
                    .send(c2)
                    .join();
        } else {
            new MessageBuilder()
                    .append("Não achei esse jogador")
                    .send(channel)
                    .join();
        }
    }
}
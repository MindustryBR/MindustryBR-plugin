package MindustryBR.Discord.Commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static MindustryBR.Main.adm;

public class BanID {
    public BanID(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        String mod_role = config.getJSONObject("discord").getString("mod_role_id");
        String adm_role = config.getJSONObject("discord").getString("admin_role_id");
        String owner_role = config.getJSONObject("discord").getString("owner_role_id");

        ServerTextChannel channel = event.getServerTextChannel().get();

        List<Role> roles = event.getMessageAuthor().asUser().get().getRoles(event.getServer().get());
        ListIterator<Role> rolesI = roles.listIterator();
        boolean tem = false;

        while (rolesI.hasNext()) {
            if (((Long) rolesI.next().getId()).toString().equals(mod_role) ||
                    ((Long) rolesI.next().getId()).toString().equals(adm_role) ||
                    ((Long) rolesI.next().getId()).toString().equals(owner_role)) {
                tem = true;
            }
        }

        if (!tem) {
            new MessageBuilder()
                    .append("Você não tem permissão para usar esse comando")
                    .send(channel)
                    .join();
            return;
        }

        adm.banPlayerID(args[1]);
    }
}

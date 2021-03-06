package MindustryBR.Discord.Commands;

import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONObject;

import static MindustryBR.Main.addPlayerAccount;
import static MindustryBR.Main.linkCodes;
import static mindustry.Vars.netServer;

public class LinkAccount {
    public LinkAccount(DiscordApi bot, JSONObject config, MessageCreateEvent event, String[] args) {
        User user = event.getMessageAuthor().asUser().get();

        if (linkCodes.get(args[1]) == null) {
            user.sendMessage("codigo invalido");
            return;
        }

        addPlayerAccount(user.getIdAsString(), linkCodes.get(args[1]));
        Administration.PlayerInfo info = netServer.admins.getInfoOptional(linkCodes.get(args[1]));
        linkCodes.remove(args[1]);

        user.sendMessage("Linkado com sucesso a conta: " + info.lastName + " (`" + info.id + "`)");
        Player player = Groups.player.find(p -> p.getInfo().id.equals(info.id));
        if (player == null) return;
        player.sendMessage("Linkado com sucesso a conta: " + user.getDiscriminatedName());
    }
}

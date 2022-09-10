package MindustryBR.Discord.Commands;

import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import static MindustryBR.internal.Util.addPlayerAccount;
import static MindustryBR.Main.linkCodes;
import static mindustry.Vars.netServer;

public class LinkAccount {
    public LinkAccount(MessageCreateEvent event, String[] args) {
        User user = event.getMessageAuthor().asUser().get();

        if (linkCodes.get(args[1]) == null) {
            user.sendMessage("codigo invalido");
            return;
        }

        addPlayerAccount(linkCodes.get(args[1]), user.getIdAsString());
        Administration.PlayerInfo info = netServer.admins.getInfoOptional(linkCodes.get(args[1]));
        linkCodes.remove(args[1]);

        user.sendMessage("Linkado com sucesso a conta: " + info.lastName + " (`" + info.id + "`)");
        Player player = Groups.player.find(p -> p.getInfo().id.equals(info.id));
        if (player != null) player.sendMessage("Linkado com sucesso a conta: " + user.getDiscriminatedName());
    }
}

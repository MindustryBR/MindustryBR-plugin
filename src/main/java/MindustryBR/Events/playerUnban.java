package MindustryBR.Events;

import MindustryBR.internal.util.Util;
import mindustry.game.EventType;
import mindustry.net.Administration;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import static mindustry.Vars.netServer;

public class playerUnban {
    public static void run (DiscordApi bot, JSONObject config, EventType.PlayerUnbanEvent e) throws IOException {
        Optional<ServerTextChannel> c1 = bot.getServerTextChannelById(config.getJSONObject("discord").getString("mod_channel_id"));
        if (c1.isEmpty()) return;
        ServerTextChannel c2 = c1.get();

        Administration.PlayerInfo player = netServer.admins.getInfoOptional(e.uuid);

        String country = Util.ip2country(player.lastIP);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(player.lastName + " foi desbanido do servidor " + config.getString("name"))
                .setDescription("UUID: `" + player.id + "`\n" +
                        "Nomes usados: `" + player.names.toString(", ") + "`\n" +
                        "Pais: `" + country + "`\n" +
                        "Entrou " + player.timesJoined + " vez(es)\n" +
                        "Kickado " + player.timesKicked + " vez(es)\n")
                .setFooter(config.getString("ip"))
                .setColor(Color.red)
                .setTimestampToNow();

        new MessageBuilder()
                .setEmbed(embed)
                .send(c2)
                .join();
    }
}

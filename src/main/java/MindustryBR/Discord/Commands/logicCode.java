package MindustryBR.Discord.Commands;

import MindustryBR.internal.Util;
import arc.struct.LongQueue;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.logic.LogicDisplay;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.nio.charset.StandardCharsets;

import static MindustryBR.internal.Util.getLocalized;

public class logicCode {
    public logicCode(MessageCreateEvent event, String[] args) {
        ServerTextChannel channel = event.getServerTextChannel().get();

        int x, y;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
        } catch (NumberFormatException ignored) {
            new MessageBuilder()
                    .append("Coordenadas invalidas")
                    .send(channel)
                    .join();
            return;
        }

        Building build = Vars.world.build(x, y);

        MessageBuilder msg = new MessageBuilder();
        EmbedBuilder embed = new EmbedBuilder()
                .setTimestampToNow()
                .setColor(Util.randomColor());


        if (build instanceof LogicBlock.LogicBuild) {
            LogicBlock.LogicBuild logicBuild = (LogicBlock.LogicBuild) build;

            embed.setTitle("Codigo do " + getLocalized(build.block().name) + " (" + x + "," + y + ")");

            if (logicBuild.code.length() > 4000) {
                embed.setDescription("```\n" + logicBuild.code.substring(0, 4000) + "...\n```");
                msg.addAttachment(logicBuild.code.getBytes(StandardCharsets.UTF_8), build.block().name + ".txt");
            } else if (logicBuild.code.length() > 0) {
                embed.setDescription("```\n" + logicBuild.code + "```");
            } else {
                embed.setDescription("Este " + getLocalized(build.block().name) + " nao tem codigo");
            }
        } else if (build instanceof LogicDisplay.LogicDisplayBuild) {
            embed.setTitle("Imagem do " + getLocalized(build.block().name));
            LogicDisplay.LogicDisplayBuild logicDisplay = (LogicDisplay.LogicDisplayBuild) build;
            if (logicDisplay.buffer != null) {
                byte[] bytes = logicDisplay.buffer.getTexture().getTextureData().getPixmap().getPixels().array();

                embed.setImage(bytes);
            } else {
                LongQueue queue = logicDisplay.commands;

                if (!queue.isEmpty()) {
                    embed.setDescription("Queue:\n" + queue);
                } else {
                    embed.setDescription("Queue vazia");
                }
            }
        } else if (build == null) {
            embed.setDescription("Nao existe um bloco construido aqui");
        } else {
            embed.setDescription("O bloco " + getLocalized(build.block().name) + " nao e valido");
        }

        msg.setEmbed(embed)
                .send(channel)
                .join();
    }
}

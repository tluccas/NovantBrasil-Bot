package org.alvesdev.commands.prefix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alvesdev.repository.CallMakerManager;

import java.awt.*;

public class CallMakerPrefixCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("v!callmaker")) return;
        Member verify = event.getMember();
        if (verify != null && !verify.hasPermission(Permission.ADMINISTRATOR)) {
            event.getChannel().sendMessage("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").queue();
        }

        Message message = event.getMessage();
        String content = message.getContentRaw();

        Member member = event.getMember();
        if (member == null) return;

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            message.reply("<:pink_error:1400136036171907183> Você precisa ser administrador para usar esse comando.").queue();
            return;
        }

        String[] args = content.split("\\s+");
        if (args.length < 2 || args.length > 3) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("<:pink_error:1400136036171907183> **Use:**");
            eb.setDescription("\nv!callmaker <id-do-canal-voz>");
            eb.setColor(Color.RED);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String channelId = args[1];
        try{
        GuildChannel channel = event.getGuild().getGuildChannelById(channelId);
            if (!(channel instanceof VoiceChannel)) {
                message.reply("<:pink_error:1400136036171907183> Canal inválido ou não é um canal de voz.").queue();
                return;
            }

            boolean saved = CallMakerManager.saveChannelId(channelId);
            if (saved) {
                message.reply("<a:check_yes2:1399848588527538375> Canal de voz modelo definido com sucesso para **" + channel.getName() + "**").queue();
            } else {
                message.reply("<:pink_error:1400136036171907183> Erro ao salvar configuração.").queue();
            }
        }catch (Exception e){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("<:pink_error:1400136036171907183> **Use:**");
            eb.setDescription("\nv!callmaker <id-do-canal-voz>");
            eb.setColor(Color.RED);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;

        }

    }
}


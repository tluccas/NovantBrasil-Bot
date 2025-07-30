package org.alvesdev.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import org.alvesdev.repository.CallMakerManager;

public class CallMakerCommand extends ListenerAdapter {

    public static SlashCommandData getCommandData() {
        return Commands.slash("callmaker", "Define um canal de voz como modelo para criação automática de calls")
                .addOption(OptionType.CHANNEL, "canal", "Canal de voz modelo", true);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("callmaker")) return;
        Member member = event.getMember();
        if(member != null && !member.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").setEphemeral(true).queue();
        }

        OptionMapping channelOpt = event.getOption("canal");
        GuildChannel channel = (channelOpt != null) ? channelOpt.getAsChannel() : null;
        if (channel == null) {
            event.reply("Erro campo de canal vazio").queue();
            return;
        }
        if (!(channel instanceof VoiceChannel)) {
            event.reply("<:pink_error:1400136036171907183> O canal precisa ser de voz.").setEphemeral(true).queue();
            return;
        }

        boolean saved = CallMakerManager.saveChannelId(channel.getId());
        if (saved) {
            event.reply("<a:check_yes2:1399848588527538375> Canal de voz modelo definido com sucesso para **" + channel.getName() +"**!").setEphemeral(true).queue();
        } else {
            event.reply("<:pink_error:1400136036171907183> Erro ao salvar configuração.").setEphemeral(true).queue();
        }
    }
}

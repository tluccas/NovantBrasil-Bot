package org.alvesdev.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SetVipRoleCommand extends ListenerAdapter {

    private static String cargoVipId;

    public static SlashCommandData getCommandData(){
        return Commands.slash("setcargovip", "Configura o cargo Vip do servidor")
                .addOption(OptionType.ROLE, "cargo", "Cargo Vip", true);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!event.getName().equals("setcargovip")) return;
        Member member = event.getMember();
        if(member != null && !member.hasPermission(Permission.ADMINISTRATOR)){
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").setEphemeral(true).queue();
            return;
        }

        OptionMapping role = event.getOption("cargo");
        String vip = (role != null) ? role.getAsString() : "";

        if(vip.isEmpty()){
            event.reply("<:pink_error:1400136036171907183> Erro ao definir cargo vip, opção vazia!").setEphemeral(true).queue();
        }
        cargoVipId = vip;
        event.reply("<a:check_yes2:1399848588527538375> Cargo Vip definido como **" + role.getAsRole().getName() + "**!").setEphemeral(true).queue();
    }

    public static String getCargoVipId(){
        return cargoVipId;
    }

}

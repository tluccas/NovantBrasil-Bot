package org.alvesdev.controller;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.alvesdev.service.VipService;

public class VipController {
    private final VipService service = new VipService();

    // /setvip
    public void handleSetVip(SlashCommandInteractionEvent event) {

        Member verify = event.getMember();
        if(verify != null && !verify.hasPermission(Permission.ADMINISTRATOR)){
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").setEphemeral(true).queue();
        }
        if (!event.isFromGuild()) {
            event.reply("<:pink_error:1400136036171907183> Este comando só pode ser usado em servidores.").setEphemeral(true).queue();
            return;
        }

        OptionMapping userOption = event.getOption("membro");
        OptionMapping daysOption = event.getOption("dias");

        if (userOption == null) {
            event.reply("<:pink_error:1400136036171907183> Você precisa especificar um usuário.").setEphemeral(true).queue();
            return;
        }

        User user = userOption.getAsUser();
        int dias = (daysOption != null) ? daysOption.getAsInt() : 0;

        if (dias <= 0) {
            event.reply("<:pink_error:1400136036171907183> Dias deve ser maior que 0.").setEphemeral(true).queue();
            return;
        }

        // Busca o membro no servidor (assíncrono para evitar bloqueios)
        event.getGuild().retrieveMember(user).queue(
                member -> {
                    // Se encontrou o membro, prossegue com a atribuição VIP
                    service.atribuirVip(member, dias);
                    event.reply("<a:check_yes2:1399848588527538375> Cargo VIP atribuído a " + member.getEffectiveName() + " por " + dias + " dias.").queue();
                },
                error -> {
                    // Se não encontrou o membro (erro)
                    event.reply("<:pink_error:1400136036171907183> Não foi possível encontrar o membro no servidor.").setEphemeral(true).queue();
                }
        );
    }
}
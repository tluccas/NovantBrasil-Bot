package org.alvesdev.controller;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.alvesdev.service.VipService;

public class VipController {
    private final VipService service = new VipService();

    // /setvip
    public void handleSetVip(SlashCommandInteractionEvent event) {

        //Fazer o bloqueio de permissões do servidor da cliente depois
        if (!event.isFromGuild()) {
            event.reply("❌ Este comando só pode ser usado em servidores.").setEphemeral(true).queue();
            return;
        }

        OptionMapping userOption = event.getOption("membro");
        OptionMapping daysOption = event.getOption("dias");

        if (userOption == null) {
            event.reply("❌ Você precisa especificar um usuário.").setEphemeral(true).queue();
            return;
        }

        User user = userOption.getAsUser();
        int dias = (daysOption != null) ? daysOption.getAsInt() : 0;

        if (dias <= 0) {
            event.reply("❌ Dias deve ser maior que 0.").setEphemeral(true).queue();
            return;
        }

        // Busca o membro no servidor (assíncrono para evitar bloqueios)
        event.getGuild().retrieveMember(user).queue(
                member -> {
                    // Se encontrou o membro, prossegue com a atribuição VIP
                    service.atribuirVip(member, dias);
                    event.reply("✅ Cargo VIP atribuído a " + member.getEffectiveName() + " por " + dias + " dias.").queue();
                },
                error -> {
                    // Se não encontrou o membro (erro)
                    event.reply("❌ Não foi possível encontrar o membro no servidor.").setEphemeral(true).queue();
                }
        );
    }
}
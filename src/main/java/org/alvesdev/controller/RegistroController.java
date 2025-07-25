package org.alvesdev.controller;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.alvesdev.service.RegistroService;

import java.util.Arrays;
import java.util.List;

public class RegistroController {

    private final RegistroService registroService = new RegistroService();

    // Handler para o comando /registro
    public void handleRegistroCommand(SlashCommandInteractionEvent event) {
        String titulo = event.getOption("titulo").getAsString();
        String mensagem = event.getOption("mensagem").getAsString();
        String imagem = event.getOption("imagem") != null ? event.getOption("imagem").getAsString() : null;
        String cargosStr = event.getOption("cargos").getAsString();

        List<String> cargos = Arrays.stream(cargosStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        registroService.enviarMenuRegistro(event, titulo, mensagem, imagem, cargos);
    }

    // Handler para a seleção do menu multi select
    public void handleSelectCargo(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("select_cargo")) {
            return;
        }

        Member member = event.getMember();
        if (member == null) {
            event.reply("Erro ao identificar seu usuário.").setEphemeral(true).queue();
            return;
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Erro ao identificar o servidor.").setEphemeral(true).queue();
            return;
        }

        for (String roleId : event.getValues()) {
            var role = guild.getRoleById(roleId);
            if (role != null) {
                if (!guild.getSelfMember().canInteract(role)) {
                    event.reply("❌ Não tenho permissão para atribuir o cargo: " + role.getName())
                            .setEphemeral(true).queue();
                    return;
                }
                if(member.getRoles().contains(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                }
                guild.addRoleToMember(member, role).queue();
            }
        }

        event.reply("Cargos atribuídos com sucesso!").setEphemeral(true).queue();
    }
}


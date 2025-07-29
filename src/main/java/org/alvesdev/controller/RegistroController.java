package org.alvesdev.controller;


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.alvesdev.service.RegistroService;

import java.util.ArrayList;
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
            return; // Ignora outros selects
        }

        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (member == null || guild == null) {
            event.reply("Erro ao identificar usuário ou servidor.").setEphemeral(true).queue();
            return;
        }

        List<String> selecionados = event.getValues(); // IDs selecionados no momento
        List<String> todosIdsDoMenu = event.getComponent().getOptions().stream()
                .map(option -> option.getValue())
                .toList();

        if (selecionados.isEmpty()) {
            event.reply("Opção desmarcada.").setEphemeral(true).queue();
            return;
        }

        StringBuilder resposta = new StringBuilder();

        for (String cargoId : todosIdsDoMenu) {
            Role role = guild.getRoleById(cargoId);
            if (role == null) continue;

            boolean possuiCargo = member.getRoles().contains(role);
            boolean foiSelecionado = selecionados.contains(cargoId);



            // Se foi selecionado e usuário não tem o cargo, adiciona
            if (foiSelecionado && !possuiCargo) {
                if (guild.getSelfMember().canInteract(role)) {
                    guild.addRoleToMember(member, role).queue();
                    resposta.append("Cargo ").append(role.getName()).append(" atribuído.\n");
                } else {
                    resposta.append("Não tenho permissão para atribuir o cargo ").append(role.getName()).append(".\n");
                }
            }

            // Se não foi selecionado e usuário possui, remove
            if (foiSelecionado && possuiCargo) {
                if (guild.getSelfMember().canInteract(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                    resposta.append("Cargo ").append(role.getName()).append(" removido.\n");
                } else {
                    resposta.append("Não tenho permissão para remover o cargo ").append(role.getName()).append(".\n");
                }
            }
        }

        event.reply(resposta.toString()).setEphemeral(true).queue();
    }
}


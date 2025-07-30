package org.alvesdev.controller;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alvesdev.service.RegistroService;

import java.util.Arrays;
import java.util.List;

public class RegistroController extends ListenerAdapter {

    private final RegistroService registroService = new RegistroService();

    // Handler para o comando slash /registro
    public void handleRegistroCommand(SlashCommandInteractionEvent event) {

        if(event.getUser().isBot()) return;

        Member member = event.getMember();
        if(member != null && !member.hasPermission(Permission.ADMINISTRATOR)){
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").queue();
        }
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

    // Handler para seleção de cargos (StringSelectMenu)
    public void handleSelectCargo(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("select_cargo")) return;

        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (member == null || guild == null) {
            event.reply("Erro ao identificar usuário ou servidor.").setEphemeral(true).queue();
            return;
        }

        List<String> selecionados = event.getValues(); // IDs selecionados
        List<String> todosIdsDoMenu = event.getComponent().getOptions().stream()
                .map(option -> option.getValue())
                .toList();

        StringBuilder resposta = new StringBuilder();

        for (String cargoId : todosIdsDoMenu) {
            Role role = guild.getRoleById(cargoId);
            if (role == null) continue;

            boolean possui = member.getRoles().contains(role);
            boolean selecionado = selecionados.contains(cargoId);

            // Adiciona se selecionado e ainda não tem
            if (selecionado && !possui) {
                if (guild.getSelfMember().canInteract(role)) {
                    guild.addRoleToMember(member, role).queue();
                    resposta.append("<a:check_yes2:1399848588527538375> Cargo **").append(role.getName()).append("** atribuído.\n");
                } else {
                    resposta.append("⚠️ Sem permissão para atribuir o cargo ").append(role.getName()).append(".\n");
                }
            }

            // Remove se não selecionado e já tem
            if (!selecionado && possui) {
                if (guild.getSelfMember().canInteract(role)) {
                    guild.removeRoleFromMember(member, role).queue();
                    resposta.append("<a:check_yes2:1399848588527538375> Cargo **").append(role.getName()).append("** removido.\n");
                } else {
                    resposta.append("⚠️ Sem permissão para remover o cargo ").append(role.getName()).append(".\n");
                }
            }
        }

        event.reply(resposta.toString()).setEphemeral(true).queue();
    }
}



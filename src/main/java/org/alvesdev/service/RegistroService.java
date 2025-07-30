package org.alvesdev.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;


import java.awt.*;
import java.util.List;

public class RegistroService {

    public void enviarMenuRegistro(SlashCommandInteractionEvent event, String titulo, String mensagem, String urlImagem, List<String> cargosIds) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(mensagem)
                .setColor(Color.CYAN);

        if (urlImagem != null && !urlImagem.isBlank()) {
            embed.setImage(urlImagem);
        }

        var guild = event.getGuild();
        if (guild == null) {
            event.reply("Erro: ao verificar id do servidor.").setEphemeral(true).queue();
            return;
        }

        var selectMenuBuilder = StringSelectMenu.create("select_cargo")
                .setPlaceholder("Selecione seus cargos")
                .setMinValues(0) // pode selecionar 0 (remover todos) até cargosIds.size()
                .setMaxValues(cargosIds.size());

        for (String roleId : cargosIds) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                selectMenuBuilder.addOption(role.getName(), roleId, "Selecionar cargo " + role.getName());
            }
        }

        var selectMenu = selectMenuBuilder.build();

        event.replyEmbeds(embed.build())
                .setComponents(ActionRow.of(selectMenu))
                .queue();
    }

    public void enviarMenuRegistroPrefix(MessageReceivedEvent event, String titulo, String mensagem, String urlImagem, List<String> cargosIds) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.getChannel().sendMessage("Erro: não foi possível identificar o servidor.").queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(mensagem)
                .setColor(Color.CYAN);

        if (urlImagem != null && !urlImagem.isBlank() && (urlImagem.startsWith("http://") || urlImagem.startsWith("https://"))) {
            embed.setImage(urlImagem);
        }

        StringBuilder cargosTexto = new StringBuilder();
        for (String roleId : cargosIds) {
            Role role = guild.getRoleById(roleId);
            if (role != null) {
                cargosTexto.append("- ").append(role.getName()).append("\n");
            }
        }

        if (cargosTexto.isEmpty()) {
            cargosTexto.append("Nenhum cargo configurado.");
        }

        embed.addField("Cargos disponíveis:", cargosTexto.toString(), false);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();

    }

}


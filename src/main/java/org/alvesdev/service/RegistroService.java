package org.alvesdev.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


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
}

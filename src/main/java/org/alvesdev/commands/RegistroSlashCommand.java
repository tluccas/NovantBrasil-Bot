package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class RegistroSlashCommand extends ListenerAdapter {

    public static SlashCommandData getCommandData(){
        return Commands.slash("registro", "Cria uma embed com seleção de cargos")
    .addOption(OptionType.STRING, "titulo", "Título da embed", true)
                .addOption(OptionType.STRING, "mensagem", "Mensagem da embed", true)
                .addOption(OptionType.STRING, "cargos", "IDs dos cargos separados por vírgula", true)
                .addOption(OptionType.STRING, "cor", "Cor hexadecimal (opcional)", false)
                .addOption(OptionType.STRING, "imagem", "URL da imagem (opcional)", false);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("registro")) return;

        String titulo = event.getOption("titulo").getAsString();
        String mensagem = event.getOption("mensagem").getAsString();
        String corHex = event.getOption("cor") != null ? event.getOption("cor").getAsString() : "#2F3136";
        String cargosRaw = event.getOption("cargos").getAsString();
        String imagem = event.getOption("imagem") != null ? event.getOption("imagem").getAsString() : null;

        Color cor;
        try {
            cor = Color.decode(corHex);
        } catch (NumberFormatException e) {
            event.reply("<:pink_error:1400136036171907183> Cor inválida. Use algo como `#00ff00`.").setEphemeral(true).queue();
            return;
        }

        List<SelectOption> opcoes = new ArrayList<>();
        for (String id : cargosRaw.split(",")) {
            Role role = event.getGuild().getRoleById(id.trim());
            if (role != null) opcoes.add(SelectOption.of(role.getName(), role.getId()));
        }

        if (opcoes.isEmpty()) {
            event.reply("<:pink_error:1400136036171907183> Nenhum cargo válido foi encontrado.").setEphemeral(true).queue();
            return;
        }

        StringSelectMenu menu = StringSelectMenu.create("registro:menu")
                .setPlaceholder("Selecione seus cargos")
                .setMinValues(0)
                .setMaxValues(opcoes.size())
                .addOptions(opcoes)
                .build();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(mensagem)
                .setColor(cor);

        if (imagem != null && !imagem.isBlank()) embed.setImage(imagem);

        event.replyEmbeds(embed.build())
                .addActionRow(menu)
                .queue();
    }
}
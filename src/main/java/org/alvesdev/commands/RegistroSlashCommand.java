package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroSlashCommand extends ListenerAdapter {

    public static SlashCommandData getCommandData() {
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


        OptionMapping tituloOpt = event.getOption("titulo");
        OptionMapping mensagemOpt = event.getOption("mensagem");
        OptionMapping cargosOpt = event.getOption("cargos");

        if (tituloOpt == null || mensagemOpt == null || cargosOpt == null) {
            event.reply("<:pink_error:1400136036171907183> Algo deu errado: campos obrigatórios não foram enviados.").setEphemeral(true).queue();
            return;
        }

        String titulo = tituloOpt.getAsString();
        String mensagem = mensagemOpt.getAsString();
        String cargosRaw = cargosOpt.getAsString();

        String corHex = event.getOption("cor") != null ? event.getOption("cor").getAsString() : "#2F3136";
        String imagem = event.getOption("imagem") != null ? event.getOption("imagem").getAsString() : null;

        Color cor;
        try {
            cor = Color.decode(corHex.startsWith("#") ? corHex : "#" + corHex);
        } catch (NumberFormatException e) {
            event.reply("<:pink_error:1400136036171907183> Cor inválida. Use algo como `#00ff00`.").setEphemeral(true).queue();
            return;
        }

        // Monta opções do SelectMenu com os cargos
        List<SelectOption> opcoes = new ArrayList<>();
        for (String id : cargosRaw.split(",")) {
            Role role = event.getGuild().getRoleById(id.trim());
            if (role != null) {
                opcoes.add(SelectOption.of(role.getName(), role.getId()));
            }
        }

        if (opcoes.isEmpty()) {
            event.reply("<:pink_error:1400136036171907183> Nenhum cargo válido foi encontrado com os IDs fornecidos.").setEphemeral(true).queue();
            return;
        }

        // Cria SelectMenu
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

        if (imagem != null && !imagem.isBlank()) {
            embed.setImage(imagem);
        }

        event.replyEmbeds(embed.build())
                .addActionRow(menu)
                .queue();
    }
}

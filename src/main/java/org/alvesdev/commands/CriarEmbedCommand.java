package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

public class CriarEmbedCommand extends ListenerAdapter {

    public static SlashCommandData getCommandData() {
        return Commands.slash("criarembed", "Cria uma embed personalizada")
                .addOption(OptionType.STRING, "titulo", "Título da embed", true)
                .addOption(OptionType.STRING, "descricao", "Descrição da embed", true)
                .addOption(OptionType.STRING, "imagem", "URL da imagem (opcional)", false)
                .addOption(OptionType.STRING, "icone", "URL do ícone da embed (opcional)", false)
                .addOption(OptionType.STRING, "cor", "Cor em hexadecimal (ex: #FF0000)", false)
                .addOption(OptionType.STRING, "footer", "Texto do rodapé (opcional)", false);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("criarembed")) return;

        Member member = event.getMember();
        if(member != null && !member.hasPermission(Permission.ADMINISTRATOR)){
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").setEphemeral(true).queue();
        }

        String titulo = event.getOption("titulo").getAsString();
        String descricao = event.getOption("descricao").getAsString();
        String imagem = event.getOption("imagem") != null ? event.getOption("imagem").getAsString() : null;
        String icone = event.getOption("icone") != null ? event.getOption("icone").getAsString() : null;
        String corHex = event.getOption("cor") != null ? event.getOption("cor").getAsString() : null;
        String footer = event.getOption("footer") != null ? event.getOption("footer").getAsString() : null;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(descricao);

        if (imagem != null) embed.setImage(imagem);
        if (icone != null) embed.setThumbnail(icone);
        if (footer != null) embed.setFooter(footer);
        if (corHex != null) {
            try {
                embed.setColor(Color.decode(corHex));
            } catch (NumberFormatException e) {
                embed.setColor(Color.GRAY);
            }
        } else {
            embed.setColor(Color.GRAY);
        }

        event.replyEmbeds(embed.build()).queue();
    }
}


package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;

public class CriarTicketCommand extends ListenerAdapter {

    public static SlashCommandData getCommandData() {
        return Commands.slash("criarticket", "Cria um painel de ticket")
                .addOption(OptionType.STRING, "titulo", "Título da embed", true)
                .addOption(OptionType.STRING, "mensagem", "Mensagem da embed", true)
                .addOption(OptionType.CHANNEL, "canal_log", "Canal de log dos tickets", true)
                .addOption(OptionType.STRING, "imagem", "URL da imagem da embed", false);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("criarticket")) return;
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("Você não tem permissão para usar este comando.").setEphemeral(true).queue();
            return;
        }


        event.deferReply().queue();

        String titulo = event.getOption("titulo").getAsString();
        String mensagem = event.getOption("mensagem").getAsString();
        TextChannel canalLog = event.getOption("canal_log").getAsChannel().asTextChannel();
        String imagem = event.getOption("imagem") != null ? event.getOption("imagem").getAsString() : null;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(mensagem)
                .setColor(Color.ORANGE);

        if (imagem != null) embed.setImage(imagem);

        StringSelectMenu menu = StringSelectMenu.create("ticket:menu")
                .setPlaceholder("Escolha uma opção")
                .addOption("Denúncia", "denuncia")
                .addOption("Sugestão", "sugestao")
                .addOption("Comprar VIP", "comprarvip")
                .build();

        event.getHook().sendMessageEmbeds(embed.build())
                .setComponents(ActionRow.of(menu))
                .queue();
    }
}


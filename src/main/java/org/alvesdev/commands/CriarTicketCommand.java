package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            event.reply("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").setEphemeral(true).queue();
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
                .addOption("Denúncia", "denuncia", "Ticket de denuncia")
                .addOption("Sugestão", "sugestao", "Ticket para sugestões")
                .addOption("Comprar VIP", "comprarvip", "Ticket para comprar seu VIP")
                .build();

        event.getHook().sendMessageEmbeds(embed.build())
                .setComponents(ActionRow.of(menu))
                .queue();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String prefix = "v!";

        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();
        if (!content.toLowerCase().startsWith(prefix + "criarticket")) return;

        Member member = event.getMember();
        if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
            event.getChannel().sendMessage("Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**").queue();
            return;
        }

        // Extrai argumentos entre aspas
        List<String> argumentosEntreAspas = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"([^\"]*)\"").matcher(content);
        while (matcher.find()) {
            argumentosEntreAspas.add(matcher.group(1));
        }

        if (argumentosEntreAspas.size() < 2) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("<:pink_error:1400136036171907183> **Use:**");
            eb.setDescription(" v!criarticket \"Título do ticket\" \"Mensagem do ticket\" #canal-log [ url da imagem opcional]");
            eb.setColor(Color.RED);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String titulo = argumentosEntreAspas.get(0);
        String mensagem = argumentosEntreAspas.get(1);

        // Obtém canal mencionado
        List<TextChannel> canaisMencionados = event.getMessage().getMentions().getChannels(TextChannel.class);
        if (canaisMencionados.isEmpty()) {
            event.getChannel().sendMessage("<:pink_error:1400136036171907183> Você precisa mencionar um canal de log.").queue();
            return;
        }
        TextChannel canalLog = canaisMencionados.get(0);

        // Captura o resto da mensagem após a menção do canal para pegar URL da imagem (opcional)
        int indexPosMenção = content.indexOf(canalLog.getAsMention()) + canalLog.getAsMention().length();
        String imagem = null;
        if (indexPosMenção < content.length()) {
            String restante = content.substring(indexPosMenção).trim();
            if (!restante.isEmpty()) {
                imagem = restante;
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(titulo)
                .setDescription(mensagem)
                .setColor(Color.ORANGE);

        if (imagem != null && !imagem.isBlank()) {
            // Pode validar se imagem começa com http/https se quiser
            embed.setImage(imagem);
        }

        // Menu de seleção
        StringSelectMenu menu = StringSelectMenu.create("ticket:menu")
                .setPlaceholder("Escolha uma opção")
                .addOption("Denúncia", "denuncia", "Ticket de denuncia")
                .addOption("Sugestão", "sugestao", "Ticket para sugestões")
                .addOption("Comprar VIP", "comprarvip", "Ticket para comprar seu VIP")
                .build();

        event.getChannel().sendMessageEmbeds(embed.build())
                .setComponents(ActionRow.of(menu))
                .queue();
    }

}


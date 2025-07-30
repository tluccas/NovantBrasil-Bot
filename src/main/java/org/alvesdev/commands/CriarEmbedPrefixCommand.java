package org.alvesdev.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CriarEmbedPrefixCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("v!criarembed")) return;

        Member member = event.getMember();
        if(member != null && !member.hasPermission(Permission.ADMINISTRATOR)){
            event.getChannel().sendMessage("\"Esse comando só pode ser usado por um **MODERADOR** ou **ADMINISTRADOR**\"").queue();
        }

        String content = event.getMessage().getContentRaw();

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"(.*?)\"").matcher(content);
        java.util.List<String> argumentos = new java.util.ArrayList<>();

        while (matcher.find()) {
            argumentos.add(matcher.group(1));
        }

        if (argumentos.size() < 2) {
            event.getChannel().sendMessage("Uso incorreto do comando!" +
                    "**Uso:** \n" +
                    "`v!criarembed \"Título\" \"Descrição\" [\"Imagem\"] [\"Ícone\"] [\"#Hex\"] [\"Footer\"]`").queue();
            return;
        }

        String titulo = argumentos.get(0);
        String descricao = argumentos.get(1);
        String imagem = argumentos.size() > 2 ? argumentos.get(2) : null;
        String icone = argumentos.size() > 3 ? argumentos.get(3) : null;
        String corHex = argumentos.size() > 4 ? argumentos.get(4) : null;
        String footer = argumentos.size() > 5 ? argumentos.get(5) : null;

        EmbedBuilder embed = new EmbedBuilder().setTitle(titulo).setDescription(descricao);

        if (imagem != null && !imagem.isBlank()) embed.setImage(imagem);
        if (icone != null && !icone.isBlank()) embed.setThumbnail(icone);
        if (footer != null && !footer.isBlank()) embed.setFooter(footer);

        if (corHex != null && !corHex.isBlank()) {
            try {
                embed.setColor(Color.decode(corHex));
            } catch (NumberFormatException e) {
                embed.setColor(Color.ORANGE);
            }
        } else {
            embed.setColor(Color.ORANGE);
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}


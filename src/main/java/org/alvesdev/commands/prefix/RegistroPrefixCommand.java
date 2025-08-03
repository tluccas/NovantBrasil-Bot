package org.alvesdev.commands.prefix;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class RegistroPrefixCommand extends ListenerAdapter {

    private static final String PREFIX = "v!";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String msg = event.getMessage().getContentRaw();
        if (!msg.startsWith(PREFIX + "registro")) return;

        String[] args = msg.substring((PREFIX + "registro").length()).trim().split(";", -1);

        if (args.length < 3) {
            event.getChannel().sendMessage("""
                <:pink_error:1400136036171907183> Uso incorreto.
                Formato correto:
                `v!registro Título;Mensagem;#cor(opcional);id1,id2;imagem(opcional)`
                """).queue();
            return;
        }

        String titulo = args[0].trim();
        String mensagem = args[1].trim();
        String corHex = !args[2].isBlank() ? args[2].trim() : "#2F3136";
        String cargosRaw = (args.length > 3) ? args[3].trim() : "";
        String imagem = (args.length > 4 && !args[4].isBlank()) ? args[4].trim() : null;

        Color cor;
        try {
            cor = Color.decode(corHex);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("<:pink_error:1400136036171907183> Cor inválida. Use `#RRGGBB`.").queue();
            return;
        }

        List<SelectOption> opcoes = new ArrayList<>();
        for (String id : cargosRaw.split(",")) {
            if (id.isBlank()) continue;
            Role role = event.getGuild().getRoleById(id.trim());
            if (role != null) opcoes.add(SelectOption.of(role.getName(), role.getId()));
        }

        if (opcoes.isEmpty()) {
            event.getChannel().sendMessage("<:pink_error:1400136036171907183> Nenhum cargo válido foi encontrado.").queue();
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

        if (imagem != null) embed.setImage(imagem);

        event.getChannel().sendMessageEmbeds(embed.build())
                .addActionRow(menu)
                .queue();
    }
}

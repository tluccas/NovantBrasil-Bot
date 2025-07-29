package org.alvesdev.repository;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketManager {

    private static final AtomicInteger ticketCount = new AtomicInteger(1);

    public static void createTicket(StringSelectInteractionEvent event, String tipo) {
        Guild guild = event.getGuild();
        Member autor = event.getMember();

        if (!(event.getChannel() instanceof TextChannel textChannel)) {
            event.reply("Erro: canal inválido para abrir ticket.").setEphemeral(true).queue();
            return;
        }

        Category categoria = textChannel.getParentCategory();

        String nomeCanal = "ticket-" + ticketCount.getAndIncrement();

        guild.createTextChannel(nomeCanal)
                .setParent(categoria)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(autor, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .queue(channel -> {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Ticket Aberto")
                            .setDescription(autor.getAsMention() + " criou um novo ticket **" + tipo + "**")
                            .setColor(Color.GREEN);

                    channel.sendMessageEmbeds(embed.build())
                            .setComponents(ActionRow.of(
                                    Button.danger("ticket:fechar", "🔒 Fechar Ticket"),
                                    Button.primary("ticket:reivindicar", "📜 Reivindicar Ticket")
                            ))
                            .queue();

                    event.reply("Seu ticket foi criado: " + channel.getAsMention()).setEphemeral(true).queue();
                });
    }


}
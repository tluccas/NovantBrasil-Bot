package org.alvesdev.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TicketButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String id = event.getComponentId();

        switch (id) {
            case "ticket:fechar" -> fecharTicket(event);
            case "ticket:reivindicar" -> reivindicarTicket(event);
        }
    }

    private void fecharTicket(ButtonInteractionEvent event) {
        event.reply("Ticket será fechado e deletado em **5 segundos**...").setEphemeral(true).queue();
        event.getChannel().delete().queueAfter(5, TimeUnit.SECONDS);
    }

    private void reivindicarTicket(ButtonInteractionEvent event) {
        Member member = event.getMember();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Ticket Reivindicado")
                .setDescription("Esse ticket foi reivindicado por " + member.getAsMention())
                .setColor(Color.BLUE)
                .build();

        event.replyEmbeds(embed).queue();
    }
}


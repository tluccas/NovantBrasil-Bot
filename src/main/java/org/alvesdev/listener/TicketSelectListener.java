package org.alvesdev.listener;

import org.alvesdev.repository.TicketManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketSelectListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("ticket:menu")) return;

        String tipo = event.getValues().get(0);
        TicketManager.createTicket(event, tipo);
    }
}
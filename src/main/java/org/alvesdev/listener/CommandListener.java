package org.alvesdev.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alvesdev.controller.VipController;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    public CommandListener(){
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("setvip")) {
            new VipController().handleSetVip(event);
        }
    }


}

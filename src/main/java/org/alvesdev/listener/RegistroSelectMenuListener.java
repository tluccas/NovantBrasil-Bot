package org.alvesdev.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;

public class RegistroSelectMenuListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("registro:menu")) return;

        Member membro = event.getMember();
        Guild guild = event.getGuild();
        List<String> selecionados = event.getValues();

        List<Role> todos = event.getComponent().getOptions().stream()
                .map(opt -> guild.getRoleById(opt.getValue()))
                .filter(Objects::nonNull)
                .toList();

        for (Role role : todos) {
            if (selecionados.contains(role.getId())) {
                if (!membro.getRoles().contains(role)) guild.addRoleToMember(membro, role).queue();
            } else {
                if (membro.getRoles().contains(role)) guild.removeRoleFromMember(membro, role).queue();
            }
        }

        event.reply("✅ Seus cargos foram atualizados!").setEphemeral(true).queue();
    }
}
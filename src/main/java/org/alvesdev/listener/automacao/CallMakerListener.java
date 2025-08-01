package org.alvesdev.listener.automacao;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.alvesdev.repository.CallMakerManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CallMakerListener extends ListenerAdapter {

    // Guarda os IDs das calls novas criadas
    private final Set<String> dynamicChannels = ConcurrentHashMap.newKeySet();

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        var joined = event.getChannelJoined();
        var member = event.getMember();
        var guild = event.getGuild();

        if (joined != null && !member.getUser().isBot()) {
            String modelChannelId = CallMakerManager.loadChannelId();
            if (modelChannelId != null && joined.getId().equals(modelChannelId)) {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    var category = joined.getParentCategory();
                    guild.createVoiceChannel("Call de " + member.getEffectiveName(), category)
                            .addPermissionOverride(member, EnumSet.of(
                                    Permission.MANAGE_CHANNEL,
                                    Permission.VIEW_CHANNEL,
                                    Permission.VOICE_CONNECT,
                                    Permission.VOICE_SPEAK), null)
                            .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                            .queue(newChannel -> {
                                // Guarda o ID no set
                                dynamicChannels.add(newChannel.getId());
                                // Move o usuário para o novo canal
                                guild.moveVoiceMember(member, newChannel).queue();
                            });
                    scheduler.shutdown();
                }, 3, TimeUnit.SECONDS);
            }
        }

        // Verificando se o canal ficou vazio
        var left = event.getChannelLeft();
        if (left != null && dynamicChannels.contains(left.getId())) {
            VoiceChannel vc = (VoiceChannel) left;
            if (vc.getMembers().isEmpty()) {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {

                    if (vc.getMembers().isEmpty()) {
                        dynamicChannels.remove(vc.getId());
                        vc.delete().queue();
                    }
                    scheduler.shutdown();
                }, 10, TimeUnit.SECONDS);

            }
        }
    }
}
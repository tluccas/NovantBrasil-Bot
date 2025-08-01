package org.alvesdev.config;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alvesdev.commands.*;
import org.alvesdev.commands.prefix.CallMakerPrefixCommand;
import org.alvesdev.commands.prefix.CriarEmbedPrefixCommand;
import org.alvesdev.controller.RegistroController;
import org.alvesdev.listener.automacao.CallMakerListener;
import org.alvesdev.listener.layout.TicketButtonListener;
import org.alvesdev.listener.layout.TicketSelectListener;
import org.alvesdev.service.VipService;
import org.alvesdev.service.registro.SlashCommandRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        var jda = event.getJDA();

        // Obtém o servidor pelo ID
        var guild = jda.getGuildById("1395994037101527111");

        //Registro de comandos

        if (guild != null) {
            guild.updateCommands()
                    .addCommands(SlashCommandRegistry.getComandos())
                    .addCommands(CriarTicketCommand.getCommandData())
                    .addCommands(CriarEmbedCommand.getCommandData())
                    .addCommands(CallMakerCommand.getCommandData())
                    .addCommands(SetVipRoleCommand.getCommandData())
                    .queue(
                            success -> System.out.println("\n[COMANDOS] Comandos registrados.\n"),
                            error -> System.err.println("\n[COMANDOS ERROR] Erro ao registrar comandos: " + error.getMessage())
                    );
        }
        //Listeners Ticket
        jda.addEventListener(new CriarTicketCommand());
        jda.addEventListener(new TicketSelectListener());
        jda.addEventListener(new TicketButtonListener());
        jda.addEventListener(new RegistroController());
        //Listeners criar embed
        jda.addEventListener(new CriarEmbedCommand());
        jda.addEventListener(new CriarEmbedPrefixCommand());
        //Listeners call maker
        jda.addEventListener(new CallMakerPrefixCommand());
        jda.addEventListener(new CallMakerListener());
        jda.addEventListener(new CallMakerCommand());

        //Listener set vip role
        jda.addEventListener(new SetVipRoleCommand());


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        VipService vipService = new VipService();

        // Verifica a cada 6 horas
        scheduler.scheduleAtFixedRate(() -> {
            vipService.removerExpirados(jda);
        }, 0, 6, TimeUnit.HOURS);
    }
}

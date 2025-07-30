package org.alvesdev.listener;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alvesdev.commands.CriarEmbedCommand;
import org.alvesdev.commands.CriarEmbedPrefixCommand;
import org.alvesdev.commands.CriarTicketCommand;
import org.alvesdev.controller.RegistroController;
import org.alvesdev.repository.TicketManager;
import org.alvesdev.service.VipService;
import org.alvesdev.service.registro.SlashCommandRegistry;

import java.time.LocalDateTime;
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
        jda.updateCommands()
                .addCommands(SlashCommandRegistry.getComandos())
                .addCommands(CriarTicketCommand.getCommandData())
                .addCommands(CriarEmbedCommand.getCommandData())
                .queue(
                        success -> System.out.println("[COMANDOS] Comandos registrados."),
                        error -> System.err.println("[COMANDOS] Erro ao registrar comandos: " + error.getMessage())
                );

        jda.addEventListener(new CriarTicketCommand());   // Listener do comando criarticket
        jda.addEventListener(new TicketSelectListener()); // Listener do select menu
        jda.addEventListener(new TicketButtonListener()); // Listener dos botões
        jda.addEventListener(new RegistroController());
        jda.addEventListener(new CriarEmbedCommand()); // Listener do criador de embed
        jda.addEventListener(new CriarEmbedPrefixCommand()); // Listener do criador de embed com prefixo



        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        VipService vipService = new VipService();

        // Verifica a cada 6 horas
        scheduler.scheduleAtFixedRate(() -> {
            vipService.removerExpirados(jda);
        }, 0, 6, TimeUnit.HOURS);
    }
}

package org.alvesdev.listener;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
                .queue(
                        success -> System.out.println("✅ Comandos registrados."),
                        error -> System.err.println("❌ Erro ao registrar comandos: " + error.getMessage())
                );


        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        VipService vipService = new VipService();

        // Verifica a cada 30 SEGUNDOS (em vez de 1 hora)
        scheduler.scheduleAtFixedRate(() -> {
            vipService.removerExpirados(jda);
            System.out.println("[TESTE] Verificação rápida em: " + LocalDateTime.now());
        }, 0, 30, TimeUnit.SECONDS);
    }
}

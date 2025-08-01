package org.alvesdev;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.alvesdev.config.Config;
import org.alvesdev.listener.CommandListener;
import org.alvesdev.config.ReadyListener;
import org.alvesdev.util.exceptions.FalhaAoIniciarException;

public class MainBot {

    public static void main(String[] args) {
         Config config = new Config();

        try{
            JDA builder = JDABuilder.createDefault(config.getToken(),
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                    GatewayIntent.SCHEDULED_EVENTS).addEventListeners(new CommandListener(), new ReadyListener()).build();

            builder.awaitReady();

        } catch (FalhaAoIniciarException e) {
            System.err.println("Erro ao iniciar o bot: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Inicialização interrompida.");
            Thread.currentThread().interrupt();
        }
    }
}

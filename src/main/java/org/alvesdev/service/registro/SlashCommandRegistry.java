package org.alvesdev.service.registro;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandRegistry {

    public static List<CommandData> getComandos() {

        List<CommandData> comandos = new ArrayList<>();

        comandos.add(Commands.slash("setvip", "Define um vip temporário")
                .addOption(OptionType.USER, "membro", "Usuário a receber o vip", true)
                .addOption(OptionType.INTEGER, "dias", "Duração do VIP em dias", true));
        comandos.add(Commands.slash("registro", "Envia o menu interativo para registro de cargos")
                .addOption(OptionType.STRING, "titulo", "Título da embed", true)
                .addOption(OptionType.STRING, "mensagem", "Mensagem da embed", true)
                .addOption(OptionType.ROLE, "cargos", "IDs dos cargos separados por vírgula", true)
                .addOption(OptionType.STRING, "imagem", "URL da imagem da embed", false)
        );
        return comandos;
    }


}

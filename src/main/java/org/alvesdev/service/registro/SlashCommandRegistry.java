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
        return comandos;
    }


}

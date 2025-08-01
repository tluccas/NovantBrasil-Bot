package org.alvesdev.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.alvesdev.commands.SetVipRoleCommand;
import org.alvesdev.model.UsuarioVip;
import org.alvesdev.repository.VipManager;

import java.time.LocalDateTime;


public class VipService {

    private final VipManager vipManager = new VipManager();


    public void atribuirVip(Member member, int dias) {
        String cargoVipId = SetVipRoleCommand.getCargoVipId();

        if (cargoVipId != null) {
            Role vipRole = member.getGuild().getRoleById(cargoVipId);
            if (vipRole == null) {
                System.err.println("\n[VIP ERROR] Cargo VIP não encontrado no servidor.\n");
                return;
            }

            if (member.getRoles().contains(vipRole)) {
                System.out.println(member.getEffectiveName() + " já possui o cargo VIP.");
                return;
            }

            member.getGuild().addRoleToMember(member, vipRole).queue(
                    success -> System.out.println("\n[VIP] VIP atribuído a " + member.getEffectiveName()),
                    error -> System.err.println("\n[VIP ERROR] Falha ao atribuir VIP: " + error.getMessage())
            );

            UsuarioVip vip = new UsuarioVip();
            vip.setUserId(member.getId());
            vip.setGuildId(member.getGuild().getId());
            vip.setRoleId(vipRole.getId());
            vip.setDataExpiracao(LocalDateTime.now().plusDays(dias));

            vipManager.adicionarVip(vip);
        } else {
            System.err.println("\n[VIP ERROR] Cargo VIP ainda não foi configurado, configure com **/setcargovip**.\n");
        }
    }

    public void removerExpirados(JDA jda) {
        vipManager.verificarExpirados(jda);
    }
}

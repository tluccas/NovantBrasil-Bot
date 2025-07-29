package org.alvesdev.service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import org.alvesdev.model.UsuarioVip;
import org.alvesdev.repository.VipManager;

import java.time.LocalDateTime;

public class VipService {

    private final VipManager vipManager = new VipManager();

    public void atribuirVip(Member member, int dias) {
        var vipRole = member.getGuild().getRoleById("1398330891788484628");

        if (vipRole != null) {
            member.getGuild().addRoleToMember(member, vipRole).queue();

            if (member.getRoles().contains(vipRole)) {
                System.out.println(member.getEffectiveName() + " já possui o cargo VIP.");
                return;
            }

            UsuarioVip vip = new UsuarioVip();
            vip.setUserId(member.getId());
            vip.setGuildId(member.getGuild().getId());
            vip.setRoleId(vipRole.getId());
            vip.setDataExpiracao(LocalDateTime.now().plusMinutes(dias));


            vipManager.adicionarVip(vip);
            member.getGuild().addRoleToMember(member, vipRole).queue(
                    success -> System.out.println("VIP atribuído a " + member.getEffectiveName()),
                    error -> System.err.println("Falha ao atribuir VIP: " + error.getMessage())
            ); // Fallback para atribuição de vip
        }
    }

    public void removerExpirados(JDA jda) {
        vipManager.verificarExpirados(jda);
    }
}

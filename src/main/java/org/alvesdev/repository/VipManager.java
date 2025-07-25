package org.alvesdev.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.alvesdev.model.UsuarioVip;
import org.alvesdev.util.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VipManager {

    private final Path arquivo = Paths.get("vips.json");
    private List<UsuarioVip> vips = new ArrayList<>();
    private final Gson gson;

    public VipManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        carregar();
    }


    private void carregar() {
        try {
            if (Files.exists(arquivo)) {
                String json = Files.readString(arquivo);
                UsuarioVip[] array = gson.fromJson(json, UsuarioVip[].class);
                if (array != null) {
                    vips = new ArrayList<>(Arrays.asList(array));
                }
            }
        } catch (IOException e) {
            System.out.println("[ VIP ] Erro ao carregar arquivo");
        }
    }

    private void salvar() {
        try {
            String json = gson.toJson(vips);
            Files.writeString(arquivo, json);
        } catch (IOException e) {
            System.out.println("[ VIP ] Erro ao salvar arquivo");
        }
    }

    // Adiciona ou atualiza VIP e salva
    public void adicionarVip(UsuarioVip novoVip) {
        for (UsuarioVip vip : vips) {
            if (vip.getUserId().equals(novoVip.getUserId()) && vip.getGuildId().equals(novoVip.getGuildId())) {
                vip.setDataExpiracao(novoVip.getDataExpiracao()); // atualiza a data
                salvar();
                return;
            }
        }

        vips.add(novoVip);
        salvar();
    }

    // Remove VIP e salva
    public void removerVip(UsuarioVip vip) {
        vips.removeIf(v -> v.getUserId().equals(vip.getUserId()) && v.getGuildId().equals(vip.getGuildId()));
        salvar();
    }

    // Retorna lista atual de VIPs
    public List<UsuarioVip> getVips() {
        return new ArrayList<>(vips);
    }

    // Verifica e remove VIPs expirados do Discord e do arquivo
    public void verificarExpirados(JDA jda) {
        LocalDateTime agora = LocalDateTime.now();

        List<UsuarioVip> expirados = new ArrayList<>();

        for (UsuarioVip vip : vips) {
            if (agora.isAfter(vip.getDataExpiracao())) {
                expirados.add(vip);
            }
        }

        for (UsuarioVip vip : expirados) {
            Guild guild = jda.getGuildById(vip.getGuildId());
            if (guild == null){
                System.out.println("[ VIP ] Guild nula ou não encontrada");
                continue;
            }

            Member member = guild.getMemberById(vip.getUserId());
            //Verifica usuarios offlines
            if (member == null){
                member = guild.retrieveMemberById(vip.getUserId()).complete();
            }
            Role role = guild.getRoleById(vip.getRoleId());

            if (role == null){
                System.err.println("[ VIP ] Cargo nulo ou não encontrado");
            }

            if (member != null && role != null) {
                guild.removeRoleFromMember(member, role).queue();
                System.out.println("[VIP] Removido VIP expirado de " + member.getEffectiveName());
            }

            removerVip(vip);
        }
    }
}

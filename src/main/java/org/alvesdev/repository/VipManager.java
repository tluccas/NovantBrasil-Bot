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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class VipManager {
    private final Path arquivo = Paths.get("vips.json");
    private final List<UsuarioVip> vips = Collections.synchronizedList(new ArrayList<>());
    private final Gson gson;
    private final Object fileLock = new Object();

    public VipManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        carregar();
    }

    private void carregar() {
        synchronized(fileLock) {
            try {
                if (Files.exists(arquivo)) {
                    String json = Files.readString(arquivo, StandardCharsets.UTF_8);
                    UsuarioVip[] array = gson.fromJson(json, UsuarioVip[].class);
                    if (array != null) {
                        vips.clear();
                        vips.addAll(Arrays.asList(array));
                        System.out.println("\n[VIP] Carregados " + vips.size() + " VIPs do arquivo\n");
                    }
                }
            } catch (Exception e) {
                System.err.println("\n[VIP ERRO] Falha ao carregar: " + e.getMessage());
            }
        }
    }

    private void salvar() {
        synchronized(fileLock) {
            try {
                String json = gson.toJson(vips);
                Files.writeString(arquivo, json, StandardCharsets.UTF_8);
            } catch (Exception e) {
                System.err.println("\n[VIP ERRO] Falha ao salvar: " + e.getMessage());
            }
        }
    }

    public void adicionarVip(UsuarioVip novoVip) {
        synchronized(vips) {
            Optional<UsuarioVip> existente = vips.stream()
                    .filter(v -> v.getUserId().equals(novoVip.getUserId())
                            && v.getGuildId().equals(novoVip.getGuildId()))
                    .findFirst();

            if (existente.isPresent()) {
                existente.get().setDataExpiracao(novoVip.getDataExpiracao());
            } else {
                vips.add(novoVip);
            }
            salvar();
        }
    }

    public void removerVip(UsuarioVip vip) {
        synchronized(vips) {
            vips.removeIf(v -> v.getUserId().equals(vip.getUserId())
                    && v.getGuildId().equals(vip.getGuildId()));
            salvar();
        }
    }

    public void verificarExpirados(JDA jda) {
        carregar();
        LocalDateTime agora = LocalDateTime.now();
        System.out.println("\n[VIP VERIFY] Iniciando verificação em: " + agora + "\n");

        List<UsuarioVip> expirados;
        synchronized(vips) {
            expirados = vips.stream()
                    .filter(vip -> {
                        boolean expirado = agora.isAfter(vip.getDataExpiracao().minusSeconds(5));
                        if (expirado) {
                            System.out.println("[\nVIP VERIFY] Expirou: " + vip.getUserId()
                                    + " | " + vip.getDataExpiracao() + "\n");
                        }
                        return expirado;
                    })
                    .toList();
        }

        expirados.forEach(vip -> processarExpirado(jda, vip));
    }

    private void processarExpirado(JDA jda, UsuarioVip vip) {
        System.out.println("[VIP] Processando expirado: " + vip.getUserId());

        Guild guild = jda.getGuildById(vip.getGuildId());
        if (guild == null) {
            System.out.println("[VIP] Guild não existe mais, removendo VIP");
            removerVip(vip);
            return;
        }

        guild.retrieveMemberById(vip.getUserId()).queue(
                member -> removerCargo(member, vip),
                error -> {
                    System.out.println("[VIP] Membro não encontrado: " + error.getMessage());
                    removerVip(vip);
                }
        );
    }

    private void removerCargo(Member member, UsuarioVip vip) {
        Guild guild = member.getGuild();
        Role role = guild.getRoleById(vip.getRoleId());

        if (role == null) {
            System.out.println("[VIP] Cargo não existe mais");
            removerVip(vip);
            return;
        }

        guild.removeRoleFromMember(member, role).queue(
                success -> {
                    System.out.println("[VIP] Cargo removido de " + member.getEffectiveName());
                    removerVip(vip);
                },
                error -> System.err.println("[VIP] Erro ao remover cargo: " + error.getMessage())
        );
    }
}
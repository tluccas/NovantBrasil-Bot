package org.alvesdev.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class UsuarioVip {

    private String userId;
    private String guildId;
    private String roleId;
    private LocalDateTime dataExpiracao;

}

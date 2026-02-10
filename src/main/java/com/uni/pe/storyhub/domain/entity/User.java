package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Integer idUsuario;
    private String nombreCompleto;
    private String username;
    private String email;
    private String password;
    private String imagenPerfil;

    @Builder.Default
    private boolean activo = false;

    @Builder.Default
    private boolean emailVerificado = false;

    private String codigoVerificacion;
    private String linkedin;
    private String telegram;
    private String instagram;
    private String facebook;
    private String youtube;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    private Integer intentosFallidos = 0;

    private LocalDateTime fechaBloqueo;

    @Builder.Default
    private String usuarioCreacion = "system";

    private String usuarioActualizacion;
}

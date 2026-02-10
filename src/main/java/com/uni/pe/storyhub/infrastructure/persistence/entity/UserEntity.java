package com.uni.pe.storyhub.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "imagen_perfil", length = 300)
    private String imagenPerfil;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = false;

    @Builder.Default
    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;

    @Column(name = "codigo_verificacion", length = 100)
    private String codigoVerificacion;

    @Column(length = 150)
    private String linkedin;

    @Column(length = 150)
    private String telegram;

    @Column(length = 150)
    private String instagram;

    @Column(length = 150)
    private String facebook;

    @Column(length = 150)
    private String youtube;

    @Column(length = 220)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    @Builder.Default
    @Column(name = "usuario_creacion", length = 100)
    private String usuarioCreacion = "system";

    @Column(name = "usuario_actualizacion", length = 100)
    private String usuarioActualizacion;
}

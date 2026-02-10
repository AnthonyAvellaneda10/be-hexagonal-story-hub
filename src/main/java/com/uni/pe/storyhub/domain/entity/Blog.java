package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {
    private Integer idBlog;
    private String titulo;
    private String breveDescripcion;
    private String imgBanner;
    private String imgPortada;
    private String descripcionImgPortada;
    private String contenidoBlog;

    @Builder.Default
    private boolean publicado = false;

    @Builder.Default
    private Integer likes = 0;

    @Builder.Default
    private Integer vistasCount = 0;

    @Builder.Default
    private boolean removed = false;
    private String slug;
    private User author;

    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    private Set<Comment> comments = new HashSet<>();
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String usuarioCreacion;
    private String usuarioActualizacion;
}

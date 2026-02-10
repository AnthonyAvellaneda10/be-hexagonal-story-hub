package com.uni.pe.storyhub.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponse {
    private Integer idBlog;
    private String titulo;
    private String breveDescripcion;
    private String imgBanner;
    private String imgPortada;
    private String descripcionImgPortada;
    private String contenidoBlog;
    private boolean publicado;
    private Integer likes;
    private Integer vistas;
    private String slug;
    private boolean isLiked;
    private UserResponse author;
    private List<TagResponse> tags;
    private LocalDateTime fechaCreacion;
}

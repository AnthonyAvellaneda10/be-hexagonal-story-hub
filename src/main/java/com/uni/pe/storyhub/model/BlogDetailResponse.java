package com.uni.pe.storyhub.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BlogDetailResponse {
    private String nombre_completo;
    private String imagen_perfil;
    private String fecha_creacion;
    private String img_banner;
    private String titulo;
    private String contenido_blog;
    private String img_portada;
    private String descripcion_img_portada;
    private List<Tags> tag;
}

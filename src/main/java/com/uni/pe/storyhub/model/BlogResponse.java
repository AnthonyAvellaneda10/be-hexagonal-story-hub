package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogResponse {
    private String imagen_perfil;
    private String titulo;
    private String breve_descripcion;
    private String img_banner;
    private String fecha_creacion;
    private User user;
}

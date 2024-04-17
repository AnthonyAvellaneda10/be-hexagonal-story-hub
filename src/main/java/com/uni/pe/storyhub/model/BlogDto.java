package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDto {
    private Integer id_blog;
    private String img_portada;
    private String titulo;
    private String breve_descripcion;
    private Boolean publicado;
    private String fecha_creacion;
    private String slug;
}
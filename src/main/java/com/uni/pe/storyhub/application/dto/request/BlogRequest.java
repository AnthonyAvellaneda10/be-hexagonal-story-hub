package com.uni.pe.storyhub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequest {
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200)
    private String titulo;

    @JsonProperty("breve_descripcion")
    @JsonAlias("breveDescripcion")
    @Size(max = 500)
    private String breveDescripcion;

    @JsonProperty("img_banner")
    @JsonAlias("imgBanner")
    private String imgBanner;

    @JsonProperty("img_portada")
    @JsonAlias("imgPortada")
    private String imgPortada;

    @JsonProperty("descripcion_img_portada")
    @JsonAlias("descripcionImgPortada")
    private String descripcionImgPortada;

    @JsonProperty("contenido_blog")
    @JsonAlias("contenidoBlog")
    @NotBlank(message = "El contenido no puede estar vacío")
    private String contenidoBlog;

    private boolean publicado;

    private List<String> tags;
}

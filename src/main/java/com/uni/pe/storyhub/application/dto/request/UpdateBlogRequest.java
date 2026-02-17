package com.uni.pe.storyhub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateBlogRequest {
    @Size(max = 200, message = "El título no puede exceder los 200 caracteres")
    private String titulo;

    @JsonProperty("breve_descripcion")
    @JsonAlias("breveDescripcion")
    @Size(max = 500, message = "La breve descripción no puede exceder los 500 caracteres")
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
    private String contenidoBlog;
    private Boolean publicado;
    private List<String> tags;
}

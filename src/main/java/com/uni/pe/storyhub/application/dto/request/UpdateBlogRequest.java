package com.uni.pe.storyhub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Size(max = 500, message = "La breve descripción no puede exceder los 500 caracteres")
    private String breveDescripcion;

    private String imgBanner;
    private String imgPortada;
    private String descripcionImgPortada;
    private String contenidoBlog;
    private Boolean publicado;
    private List<String> tags;
}

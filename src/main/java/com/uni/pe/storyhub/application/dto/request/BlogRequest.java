package com.uni.pe.storyhub.application.dto.request;

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

    @Size(max = 500)
    private String breveDescripcion;

    private String imgBanner;
    private String imgPortada;
    private String descripcionImgPortada;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String contenidoBlog;

    private boolean publicado;

    private List<String> tags;
}

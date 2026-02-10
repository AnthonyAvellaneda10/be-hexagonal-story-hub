package com.uni.pe.storyhub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateUserRequest {

    @Size(max = 100, message = "El nombre de usuario no puede exceder los 100 caracteres")
    private String username;

    @Size(max = 220, message = "La descripción no puede exceder los 220 caracteres")
    private String descripcion;

    @Size(max = 150, message = "El link de LinkedIn no puede exceder los 150 caracteres")
    private String linkedin;

    @Size(max = 150, message = "El link de Telegram no puede exceder los 150 caracteres")
    private String telegram;

    @Size(max = 150, message = "El link de Instagram no puede exceder los 150 caracteres")
    private String instagram;

    @Size(max = 150, message = "El link de Facebook no puede exceder los 150 caracteres")
    private String facebook;

    @Size(max = 150, message = "El link de YouTube no puede exceder los 150 caracteres")
    private String youtube;
}

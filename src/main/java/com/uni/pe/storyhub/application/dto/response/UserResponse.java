package com.uni.pe.storyhub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Integer idUsuario;
    private String email;
    private String imagenPerfil;
    private String username;
    private String fullName;
    private String descripcion;
    private String linkedin;
    private String telegram;
    private String instagram;
    private String facebook;
    private String youtube;
}

package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUserProfileResponse {
    private String imagen_perfil;
    private String email;
    private String nombre_completo;
    private String username;
    private String descripcion;
    private String linkedin;
    private String telegram;
    private String youtube;
    private String instagram;
    private String facebook;
}

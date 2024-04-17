package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileBlogResponse {
    private String nombre_completo;
    private String username;
    private String email;
    private String imagen_perfil;
    private String linkedin;
    private String telefono;
    private String instagram;
    private String twitter;
    private String descripcion;
    private String fecha_creacion;
    private List<BlogResponse> blog;
}

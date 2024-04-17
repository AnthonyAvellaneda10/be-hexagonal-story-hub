package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDao {
    private Integer id_usuario;
    private String nombre_completo;
    private String username;
    private String email;
    private String contraseña;
    private String imagen_perfil;
    private String linkedin;
    private String telefono;
    private String instagram;
    private String twitter;
    private String descripcion;
}

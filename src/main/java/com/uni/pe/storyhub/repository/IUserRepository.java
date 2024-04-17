package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.UserProfileBlogResponse;
import com.uni.pe.storyhub.model.UserResponse;

public interface IUserRepository {
    boolean existeCorreo(String email);

    boolean existeNombreUsuario(String username);

    boolean registrarUsuario(String nombre_completo, String username, String correo, String contraseña, String imagen_perfil);

    String obtenerContraseñaPorEmail(String email);

    UserResponse obtenerDataDelUsuarioPorEmail(String email);
    UserProfileBlogResponse getUserProfile(String username);
}

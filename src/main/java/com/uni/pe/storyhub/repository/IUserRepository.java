package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.*;

public interface IUserRepository {
    boolean existeCorreo(String email);

    boolean existeNombreUsuario(String username);

    boolean registrarUsuario(String nombre_completo, String username, String correo, String contraseña, String imagen_perfil);

    String obtenerContraseñaPorEmail(String email);

    UserResponse obtenerDataDelUsuarioPorEmail(String email);
    UserProfileBlogResponse getUserProfile(String username);

    int actualizarFotoDePerfil(UpdatePhotoUser updatePhotoUser);

    int actualizarPerfil(UpdateProfileUser updateProfileUser);

    GetUserProfileResponse obtenerPerfilDelUsuarioPorEmail(String email);
}

package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.*;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    Alert registrarUsuario(UserDtoRegistro usuarioDto);

    Alert loguearUsuario(UserDtoLogin usuarioLoguear);

    ResponseEntity<?> obtenerPerfilDelUsuario(String username);

    Alert actualizarFotoDePerfil(UpdatePhotoUser updatePhotoUser);

    Alert actualizarPerfil(UpdateProfileUser updateProfileUser);

    ResponseEntity<?> obtenerPerfilDelUsuarioPorEmail(String email);
}

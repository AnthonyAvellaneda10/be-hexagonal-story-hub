package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.Alert;
import com.uni.pe.storyhub.model.UserDtoRegistro;
import com.uni.pe.storyhub.model.UserDtoLogin;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    Alert registrarUsuario(UserDtoRegistro usuarioDto);

    Alert loguearUsuario(UserDtoLogin usuarioLoguear);

    ResponseEntity<?> obtenerPerfilDelUsuario(String username);
}

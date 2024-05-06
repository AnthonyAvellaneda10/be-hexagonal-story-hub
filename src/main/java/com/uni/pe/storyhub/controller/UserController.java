package com.uni.pe.storyhub.controller;

import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" })
public class UserController {
    @Autowired
    private IUserService iUserService;

    @PostMapping("/auth/registro")
    public ResponseEntity<?> registroUsuario(@RequestBody UserDtoRegistro usuario){

        Alert alerta = iUserService.registrarUsuario(usuario);
        HttpStatus status = HttpStatus.OK;

        if (alerta.getType().equals("danger")) {
            status = HttpStatus.NOT_FOUND; // Código 404 para errores
        } else if (alerta.getType().equals("success")) {
            status = HttpStatus.CREATED; // Código 201 para éxito
        } else if(alerta.getType().equals("warning")){
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Cambiar a 500 para errores internos del servidor
        }

        return ResponseEntity.status(status).body(alerta);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUsuario(@RequestBody UserDtoLogin usuarioLogin){

        Alert alerta = iUserService.loguearUsuario(usuarioLogin);
        HttpStatus status = HttpStatus.OK;

        if (alerta.getType().equals("danger")) {
            status = HttpStatus.NOT_FOUND; // Código 404 para errores
        } else if (alerta.getType().equals("success")) {
            status = HttpStatus.CREATED; // Código 201 para éxito
        } else if(alerta.getType().equals("warning")){
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Cambiar a 500 para errores internos del servidor
        }

        return ResponseEntity.status(status).body(alerta);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> obtenerInformacionDelUsuario(@PathVariable("username") String username) {
        return iUserService.obtenerPerfilDelUsuario(username);
    }

    @PostMapping("/update/photo")
    public ResponseEntity<?> actualizarFotoDePerfil(@RequestBody UpdatePhotoUser updatePhotoUser){

        Alert alerta = iUserService.actualizarFotoDePerfil(updatePhotoUser);
        HttpStatus status = HttpStatus.OK;

        if (alerta.getType().equals("danger")) {
            status = HttpStatus.NOT_FOUND; // Código 404 para errores
        } else if (alerta.getType().equals("success")) {
            status = HttpStatus.CREATED; // Código 201 para éxito
        } else if(alerta.getType().equals("warning")){
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Cambiar a 500 para errores internos del servidor
        }

        return ResponseEntity.status(status).body(alerta);
    }

    @PostMapping("/update/profile")
    public ResponseEntity<?> actualizarPerfil(@RequestBody UpdateProfileUser updateProfileUser){

        Alert alerta = iUserService.actualizarPerfil(updateProfileUser);
        HttpStatus status = HttpStatus.OK;

        if (alerta.getType().equals("danger")) {
            status = HttpStatus.NOT_FOUND; // Código 404 para errores
        } else if (alerta.getType().equals("success")) {
            status = HttpStatus.CREATED; // Código 201 para éxito
        } else if(alerta.getType().equals("warning")){
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Cambiar a 500 para errores internos del servidor
        }

        return ResponseEntity.status(status).body(alerta);
    }

    @GetMapping("/get-profile/{email}")
    public ResponseEntity<?> getProfileUser(@PathVariable("email") String email) {
        return iUserService.obtenerPerfilDelUsuarioPorEmail(email);
    }

    @PutMapping("/update/password")
    public ResponseEntity<?> actualizarContraseña(@RequestBody UpdatePassword updatePassword){

        Alert alerta = iUserService.actualizarContraseña(updatePassword);
        HttpStatus status = HttpStatus.OK;

        if (alerta.getType().equals("danger")) {
            status = HttpStatus.NOT_FOUND; // Código 404 para errores
        } else if (alerta.getType().equals("success")) {
            status = HttpStatus.CREATED; // Código 201 para éxito
        } else if(alerta.getType().equals("warning")){
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Cambiar a 500 para errores internos del servidor
        }

        return ResponseEntity.status(status).body(alerta);
    }

    @GetMapping("/photo/{email}")
    public ResponseEntity<?> obtenerFotoDePerfil(@PathVariable("email") String email) {
        return iUserService.obtenerFotoDePerfil(email);
    }
}

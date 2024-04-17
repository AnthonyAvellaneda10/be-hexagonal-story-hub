package com.uni.pe.storyhub.controller;

import com.uni.pe.storyhub.model.Alert;
import com.uni.pe.storyhub.model.UserDtoLogin;
import com.uni.pe.storyhub.model.UserDtoRegistro;
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
}

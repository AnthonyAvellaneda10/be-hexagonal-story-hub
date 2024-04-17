package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.repository.IUserRepository;
import com.uni.pe.storyhub.utils.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository iUserRepository;
    Utilidades utilidades = new Utilidades();
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Alert registrarUsuario(UserDtoRegistro usuarioDto) {

        try {
            String nombreCompleto = usuarioDto.getNombre_completo();
            String email = usuarioDto.getEmail();
            String contraseña = usuarioDto.getContraseña();
            
            String username = null;
            if (nombreCompleto != null) {
                username = utilidades.extraerPrimerNombre(nombreCompleto);
            }

            // Verificar el formato del correo electrónico
            if (!utilidades.isValidEmailFormat(email)) {
                // El formato del correo no es válido, devolver un TestResponse con mensaje de error
                return new Alert(Utilidades.getNextAlertId(), "El formato de email no es válido", 5000, "danger", 404);
            }

            // Verificar si el correo esta escrito en minúsculas
            if (!(email == email.toLowerCase())) {

                return new Alert(Utilidades.getNextAlertId(), "Solo se permiten letras (a-z), números (0-9)  y puntos (.)", 5000, "danger", 404);
            }

            // Verificar si el correo ya existe en la base de datos
            if (iUserRepository.existeCorreo(email)) {
                return new Alert(Utilidades.getNextAlertId(), "Este correo ya existe", 5000, "danger", 404);
            }

            // Verificar si el nombre de usuario ya existe en la base de datos
            if (iUserRepository.existeNombreUsuario(username)) {
                return new Alert(Utilidades.getNextAlertId(), "El nombre de usuario ya existe", 5000, "danger", 404);
            }

            // Encriptando la contraseña del usuario
            String encodedPassword = passwordEncoder.encode(contraseña);

            UserDao obtenerUserDao = setearUsuario( nombreCompleto,  username,  email,  encodedPassword);

            // Insertar usuario en la base de datos
            boolean registroExitoso = iUserRepository.registrarUsuario(obtenerUserDao.getNombre_completo(), obtenerUserDao.getUsername(), obtenerUserDao.getEmail(), obtenerUserDao.getContraseña(), obtenerUserDao.getImagen_perfil());

            if (registroExitoso) {
                return new Alert(Utilidades.getNextAlertId(), "Registro exitoso", 5000, "success", 201);
            } else {
                return new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salió mal", 5000, "warning", 500);
            }

        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
            // Devolver una respuesta de error
            return new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salió mal", 5000, "warning", 500);
        }

    }

    @Override
    public Alert loguearUsuario(UserDtoLogin usuarioLoguear) {
        try {
            // Obtener el correo del Usuario
            String email = usuarioLoguear.getEmail();
            // Obtener la contraseña del Usuario
            String contraseña = usuarioLoguear.getContraseña();

            // Verificar el formato del correo electrónico
            if (!utilidades.isValidEmailFormat(email)) {
                return new Alert(Utilidades.getNextAlertId(), "El formato de correo electrónico no es válido", 5000, "danger", 404);
            }

            // Verificar si el usuario existe en la base de datos
            if (!iUserRepository.existeCorreo(email)) {
                return new Alert(Utilidades.getNextAlertId(), "Usuario no encontrado", 5000, "danger", 404);
            }

            // Obtener la contraseña asociada con el correo electrónico proporcionado por el usuario
            String contraseñaAlmacenada = iUserRepository.obtenerContraseñaPorEmail(usuarioLoguear.getEmail());

            // Comparar la contraseña almacenada con la proporcionada por el usuario
            if (!passwordEncoder.matches(contraseña, contraseñaAlmacenada)) {
                // La contraseña no coincide
                return new Alert(Utilidades.getNextAlertId(), "Contraseña incorrecta", 5000, "danger", 404);
            } else {
                UserResponse userResponse = iUserRepository.obtenerDataDelUsuarioPorEmail(email);
                // Inicio de sesión exitoso
                return new Alert(Utilidades.getNextAlertId(), "Inicio de sesión exitoso", 5000, "success", 200, userResponse);
            }

        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
            // Devolver una respuesta de error
            return new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salió mal", 5000, "warning", 500);
        }
    }

    @Override
    public ResponseEntity<?> obtenerPerfilDelUsuario(String username) {


        try {
            // Verificar si el nickname del usuario existe
            if (!iUserRepository.existeNombreUsuario(username)){
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe dicho usuario", 5000, "warning", 500);;;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            UserProfileBlogResponse userProfile = iUserRepository.getUserProfile(username);
            return ResponseEntity.ok(userProfile);

        } catch (Exception e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);;;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }

    }


    private UserDao setearUsuario(String nombreCompleto, String username, String email, String encodedPassword) {
        // Crear el UserDto
        UserDao usuarioDao = new UserDao();
        usuarioDao.setNombre_completo(nombreCompleto);
        usuarioDao.setUsername(username);
        usuarioDao.setEmail(email);
        usuarioDao.setContraseña(encodedPassword);
        usuarioDao.setImagen_perfil("assets/images/user-profile.png");

        return usuarioDao;
    }

}

package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.repository.IBlogRepository;
import com.uni.pe.storyhub.utils.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService implements  IBlogService {
    @Autowired
    private IBlogRepository iBlogRepository;

    @Override
    public Alert añadirBlog(Blog blogRequest) {

        try {
            Integer idUsuario = blogRequest.getUserIDResponse().getId_usuario();

            // Verificar si el correo ya existe en la base de datos
            if (!iBlogRepository.existeIdUsuario(idUsuario)) {
                return new Alert(Utilidades.getNextAlertId(), "No existe dicho usuario", 5000, "danger", 404);
            }


            // Limpiar el texto del título del blog
            String tituloLimpio = Utilidades.cleanText(blogRequest.getTitulo());
            // Generar el slug del título del blog
            String slug = Utilidades.generarSlug(blogRequest.getTitulo());

            // Asignar el slug al objeto Blog
            blogRequest.setTitulo(tituloLimpio);
            blogRequest.setSlug(slug);

            // Insertar usuario en la base de datos
            boolean añadirBlog = iBlogRepository.añadirBlog(blogRequest);

            if (añadirBlog) {
                return new Alert(Utilidades.getNextAlertId(), "Se añadio el blog", 5000, "success", 201);
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
    public ResponseEntity<?> obtenerListaDeBlogs(String email) {

        try {
            Utilidades utilidades = new Utilidades();


            // Verificar el formato del correo electrónico
            if (!utilidades.isValidEmailFormat(email)) {
                // El formato del correo no es válido, devolver un TestResponse con mensaje de error
                Alert alert = new Alert(Utilidades.getNextAlertId(), "El formato de email no es válido", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            // Verificar si el correo ya existe en la base de datos
            if (!iBlogRepository.existeEmailDelUsuario(email)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe correo", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            List<BlogDto> blogs = iBlogRepository.obtenerBlogsDelUsuario(email);

            if (blogs.isEmpty()) {
                // Si la lista de blogs está vacía, devuelve un Alert
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No tienes blogs creados \uD83E\uDD7A. Comienza a crear uno ✍\uD83C\uDFFB.", 5000, "danger", 404);;

                // Aquí puedes configurar otros campos del objeto Alert según necesites
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
                //return new Alert(Utilidades.getNextAlertId(), "Solo se permiten letras (a-z), números (0-9)  y puntos (.)", 5000, "danger", 404);
            } else {
                // Si la lista de blogs no está vacía, devuélvela directamente
                return ResponseEntity.ok(blogs);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Error al obtener los blogs del usuario", 5000, "warning", 500);;;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> buscarTodosLosBlogs() {

        try{
            List<BlogResponse> blogs = iBlogRepository.obtenerTodosLosBlogsCreados();

            if (blogs.isEmpty()) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No hay datos disponibles", 5000, "danger", 404);;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                return ResponseEntity.ok(blogs);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> obtenerInformacionDelBlog(String slug) {


        try {
            // Verificar si el slug del blog existe
            if (!iBlogRepository.existeSlug(slug)){
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 500);;;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                // Obtener el estado de publicación del blog
                boolean esPublico = iBlogRepository.esPublico(slug);

                // Verificar si el blog no es público
                if (!esPublico) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Este blog no es público", 5000, "warning", 500);;
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(alert);
                } else {
                    // Obtener la información del blog
                    BlogDetailResponse blogDetail = iBlogRepository.obtenerDetalleDelBlog(slug);
                    return ResponseEntity.ok(blogDetail);
                }
            }



        } catch (Exception e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);;;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }

    }
}

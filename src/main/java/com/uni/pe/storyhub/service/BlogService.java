package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.repository.IBlogRepository;
import com.uni.pe.storyhub.utils.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BlogService implements IBlogService {
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

        } catch (DataIntegrityViolationException e) {
            // Manejar la excepción de clave duplicada (nombre de usuario duplicado)
            return new Alert(Utilidades.getNextAlertId(), e.getMessage(), 5000, "warning", 500);
        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
            // Devolver una respuesta de error
            return new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salió mal", 5000, "warning", 500);
        }
    }

    @Override
    public ResponseEntity<?> obtenerListaDeBlogs(String email, int page, int size) {
        try {
            Utilidades utilidades = new Utilidades();
            // Verificar el formato del correo electrónico
            if (!utilidades.isValidEmailFormat(email)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "El formato de email no es válido", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            // Verificar si el correo ya existe en la base de datos
            if (!iBlogRepository.existeEmailDelUsuario(email)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe correo", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            Page<BlogDto> blogs = iBlogRepository.obtenerBlogsDelUsuario(email, PageRequest.of(page, size));

            if (blogs.isEmpty()) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No tienes blogs creados \uD83E\uDD7A. Comienza a crear uno ✍\uD83C\uDFFB.", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                return ResponseEntity.ok(blogs);
            }

        } catch (Exception e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Error al obtener los blogs del usuario", 5000, "warning", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> buscarTodosLosBlogs(Pageable pageable) {
        try {
            Page<BlogResponse> blogs = iBlogRepository.obtenerTodosLosBlogsCreados(pageable);

            if (blogs.isEmpty()) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No hay datos disponibles", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                return ResponseEntity.ok(blogs);
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "No hay datos disponibles", 5000, "danger", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> obtenerTagsCreados() {
        try {
            List<Tags> tags = iBlogRepository.obtenerTags();

            if (tags.isEmpty()) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No hay tags creados, comience creando uno", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                return ResponseEntity.ok(tags);
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Utilidades.getNextAlertId(), "No hay datos disponibles", 5000, "danger", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> obtenerInformacionDelBlog(String slug, Integer idUsuario) {
        try {
            // Verificar si el slug del blog existe
            if (!iBlogRepository.existeSlug(slug)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 500);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                // Obtener el estado de publicación del blog
                boolean esPublico = iBlogRepository.esPublico(slug);

                // Verificar si el blog no es público
                if (!esPublico) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Este blog no es público", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(alert);
                } else {
                    int count = iBlogRepository.blogEliminadoBySlug(slug);
                    if (!(count > 0)) {
                        Alert alert = new Alert(Utilidades.getNextAlertId(), "Este blog fue eliminado", 5000, "danger", 404);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
                    } else {
                        // Obtener la información del blog
                        // Ejecutar la consulta de actualización
                        int vistasActualizadas = iBlogRepository.actualizarVistasDelBlog(slug);
                        if (vistasActualizadas > 0) {
                            int idBlog = iBlogRepository.obtenerIdBlogBySlug(slug);
                            BlogDetailResponse blogDetail = iBlogRepository.obtenerDetalleDelBlog(slug, idBlog, idUsuario);
                            return ResponseEntity.ok(blogDetail);
                        } else {
                            // Si no se actualizó ninguna fila, mostrar un mensaje de error
                            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salió mal al actualizar las vistas del blog", 5000, "warning", 500);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> darLikeAlBlog(Integer idUsuario, Integer idBlog) {
        try {
            // Verificar si el slug del blog existe
            if (!iBlogRepository.existeIdUsuario(idUsuario)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este usuario", 5000, "warning", 500);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            if (!iBlogRepository.existeIdBlog(idBlog)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 500);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            int count = iBlogRepository.verificarSiDioLike(idUsuario, idBlog);
            if (count == 0) {
                int filasAfectadas = iBlogRepository.darLikePorPrimeraVez(idUsuario, idBlog);
                if (filasAfectadas > 0) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Se registró el like", 5000, "success", 201);
                    return ResponseEntity.ok(alert);
                } else {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salio mal al dar like", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                }
            }

            boolean estadoDeLike = iBlogRepository.verEstadoDeLike(idUsuario, idBlog);

            if (estadoDeLike) {
                int filasAfectadas = iBlogRepository.quitarLike(idUsuario, idBlog);
                if (filasAfectadas > 0) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Like quitado con exito", 5000, "success", 201);
                    return ResponseEntity.ok(alert);
                } else {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salio mal al dar like", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                }
            } else {
                int filasAfectadas = iBlogRepository.agregarLike(idUsuario, idBlog);
                if (filasAfectadas > 0) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Like dado con exito", 5000, "success", 201);
                    return ResponseEntity.ok(alert);
                } else {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salio mal al dar like", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> verificarLike(Integer idUsuario, Integer idBlog) {
        try {
            // Verificar si el slug del blog existe
            if (!iBlogRepository.existeIdUsuario(idUsuario)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este usuario", 5000, "warning", 500);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            if (!iBlogRepository.existeIdBlog(idBlog)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 500);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            boolean existeLike = iBlogRepository.existeLike(idUsuario, idBlog);
            return ResponseEntity.ok(existeLike);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> editarBlog(Integer idBlog, String breveDescripcion, String contenidoBlog, Boolean publicado) {
        try {
            // Verificar si el blog existe o no
            if (!iBlogRepository.existeIdBlog(idBlog)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            boolean blogEliminado = iBlogRepository.blogEliminado(idBlog);

            if (blogEliminado) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "Este blog ya fue eliminado", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                int filasAfectadas = iBlogRepository.editarBlog(idBlog, breveDescripcion, contenidoBlog, publicado);
                if (filasAfectadas > 0) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Se editó el blog", 5000, "success", 201);
                    return ResponseEntity.ok(alert);
                } else {
                    // Si no se actualizó ninguna fila, mostrar un mensaje de error
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salió mal al editar el blog", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);
            ;
            ;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }

    @Override
    public ResponseEntity<?> eliminarBlog(Integer idBlog) {
        try {
            // Verificar si el blog existe o no
            if (!iBlogRepository.existeIdBlog(idBlog)) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "No existe este blog", 5000, "warning", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            }

            boolean blogEliminado = iBlogRepository.blogEliminado(idBlog);

            if (blogEliminado) {
                Alert alert = new Alert(Utilidades.getNextAlertId(), "Este blog ya fue eliminado", 5000, "danger", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alert);
            } else {
                int filasAfectadas = iBlogRepository.eliminarBlog(idBlog);
                if (filasAfectadas > 0) {
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Se eliminó el blog", 5000, "success", 201);
                    return ResponseEntity.ok(alert);
                } else {
                    // Si no se actualizó ninguna fila, mostrar un mensaje de error
                    Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, algo salió mal al eliminar el blog", 5000, "warning", 500);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Utilidades.getNextAlertId(), "Ups, parece que algo salio mal", 5000, "warning", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alert);
        }
    }
}

package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.ComentarioBlog;
import com.uni.pe.storyhub.model.ErrorResponse;
import com.uni.pe.storyhub.repository.IBlogRepository;
import com.uni.pe.storyhub.repository.IComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComentarioService implements IComentarioService {
    @Autowired
    private IComentarioRepository iComentarioRepository;

    @Autowired
    private IBlogRepository iBlogRepository;

    @Override
    public ResponseEntity<?> obtenerComentariosBlog(String slug) {
        try {
            if (!iBlogRepository.existeSlugBlog(slug)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No existe este blog"));
            }

            if (!iBlogRepository.esPublico(slug)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("No puedes ver este blog"));
            }

            int idBlog = iBlogRepository.obtenerIdBlogBySlug(slug);

            List<ComentarioBlog> comentarios = iComentarioRepository.obtenerComentariosBlog(idBlog);

            // Create a map to easily find comments by id
            Map<Integer, ComentarioBlog> comentarioMap = new HashMap<>();
            for (ComentarioBlog comentario : comentarios) {
                comentarioMap.put(comentario.getId_usuario(), comentario);
            }

            // Add replies to the parent comments
            for (ComentarioBlog comentario : comentarios) {
                if (comentario.getParent_comentario_id() != null) {
                    ComentarioBlog parent = comentarioMap.get(comentario.getParent_comentario_id());
                    if (parent != null) {
                        if (parent.getReplies() == null) {
                            parent.setReplies(new ArrayList<>());
                        }
                        parent.getReplies().add(comentario.getId_usuario());
                    }
                }
            }

            // Collect all comments to include in the final response
            List<ComentarioBlog> allComentarios = comentarios.stream().collect(Collectors.toList());
            Map<String, List<ComentarioBlog>> response = new HashMap<>();
            response.put("comments", allComentarios);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Ups, parece que algo salio mal"));
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> publicarComentario(String comentario, Integer parent_comentario_id, String reply_to, int idUsuario, int idBlog) {

        Map<String, String> response = new HashMap<>();
        try {

            if (!iBlogRepository.existeIdBlog(idBlog)) {
                response.put("message", "No existe este profesor");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!iBlogRepository.existeIdUsuario(idUsuario)) {
                response.put("message", "No existe este usuario");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            int count = iComentarioRepository.publicarComentario(comentario, parent_comentario_id, reply_to, idUsuario, idBlog);

            if (count > 0) {
                response.put("message", "Se registró su comentario");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "No se registró nada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (EmptyResultDataAccessException e) {
            response.put("message", "No se pudo registrar su comentario");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataAccessException e) {
            response.put("message", "Inténtelo más tarde");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> editarComentario(String comentario, int idComentario) {
        Map<String, String> response = new HashMap<>();
        try {
            int filasAfectadas = iComentarioRepository.actualizarComentario(comentario, idComentario);
            if (filasAfectadas > 0) {
                response.put("message", "Se editó su comentario");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // Si no se actualizó ninguna fila, mostrar un mensaje de error
                response.put("message", "Ups, algo salió mal al tratar de actualizar su comentario");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EmptyResultDataAccessException e) {
            response.put("message", "No se pudo editar su comentario");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataAccessException e) {
            response.put("message", "Inténtelo más tarde");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> eliminarComentario(int idComentario) {
        Map<String, String> response = new HashMap<>();
        try {
            int filasAfectadas = iComentarioRepository.eliminarComentario(idComentario);
            if (filasAfectadas > 0) {
                response.put("message", "Se eliminó su comentario");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // Si no se actualizó ninguna fila, mostrar un mensaje de error
                response.put("message", "Ups, algo salió mal al tratar de eliminar su comentario");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EmptyResultDataAccessException e) {
            response.put("message", "No se pudo eliminar su comentario");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataAccessException e) {
            response.put("message", "Inténtelo más tarde");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.uni.pe.storyhub.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IComentarioService {
    ResponseEntity<?> obtenerComentariosBlog(int idBlog);
    ResponseEntity<Map<String, String>> publicarComentario(String comentario, Integer parent_comentario_id, String reply_to, int idUsuario, int idBlog);
    ResponseEntity<Map<String, String>> editarComentario(String comentario, int idComentario);
    ResponseEntity<Map<String, String>> eliminarComentario(int idComentario);
}

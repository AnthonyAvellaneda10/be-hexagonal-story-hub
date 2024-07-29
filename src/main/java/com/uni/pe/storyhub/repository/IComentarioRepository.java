package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.ComentarioBlog;

import java.util.List;

public interface IComentarioRepository {
    List<ComentarioBlog> obtenerComentariosBlog(int idBlog);
    int publicarComentario(String comentario, Integer parent_comentario_id, String reply_to, int idUsuario, int idBlog);
    int actualizarComentario(String comentarioEdit, int idComentario);
    int eliminarComentario(int idComentario);
}

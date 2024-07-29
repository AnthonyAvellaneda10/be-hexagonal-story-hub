package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.Alert;
import com.uni.pe.storyhub.model.Blog;
import com.uni.pe.storyhub.model.BlogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBlogService {
    Alert añadirBlog(Blog blogRequest);

    ResponseEntity<?> obtenerListaDeBlogs(String email, int page, int size);

    ResponseEntity<?> buscarTodosLosBlogs(Pageable pageable);
    ResponseEntity<?> obtenerTagsCreados();

    ResponseEntity<?> obtenerInformacionDelBlog(String slug, Integer idUsuario);
    ResponseEntity<?> verificarLike(Integer idUsuario, Integer idBlog);
    ResponseEntity<?> darLikeAlBlog(Integer idUsuario, Integer idBlog);

    ResponseEntity<?> eliminarBlog(Integer idBlog);
    ResponseEntity<?> editarBlog(Integer idBlog, String breveDescripcion, String contenidoBlog, Boolean publicado);
}

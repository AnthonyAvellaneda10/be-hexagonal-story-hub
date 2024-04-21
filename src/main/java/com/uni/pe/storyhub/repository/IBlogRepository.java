package com.uni.pe.storyhub.repository;

import com.uni.pe.storyhub.model.Blog;
import com.uni.pe.storyhub.model.BlogDetailResponse;
import com.uni.pe.storyhub.model.BlogDto;
import com.uni.pe.storyhub.model.BlogResponse;

import java.util.List;

public interface IBlogRepository {
    boolean añadirBlog(Blog blogRequest);

    boolean existeIdUsuario(Integer idUsuario);

    boolean existeEmailDelUsuario(String email);

    List<BlogDto> obtenerBlogsDelUsuario(String email);

    List<BlogResponse> obtenerTodosLosBlogsCreados();

    BlogDetailResponse obtenerDetalleDelBlog(String slug);

    boolean existeSlug(String slug);

    boolean esPublico(String slug);
}

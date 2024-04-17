package com.uni.pe.storyhub.service;

import com.uni.pe.storyhub.model.Alert;
import com.uni.pe.storyhub.model.Blog;
import com.uni.pe.storyhub.model.BlogResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBlogService {
    Alert añadirBlog(Blog blogRequest);

    ResponseEntity<?> obtenerListaDeBlogs(String email);

    ResponseEntity<?> buscarTodosLosBlogs();
}

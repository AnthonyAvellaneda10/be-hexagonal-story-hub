package com.uni.pe.storyhub.controller;

import com.uni.pe.storyhub.model.Alert;
import com.uni.pe.storyhub.model.Blog;
import com.uni.pe.storyhub.model.BlogResponse;
import com.uni.pe.storyhub.service.IBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" })
public class BlogController {
    @Autowired
    private IBlogService iBlogService;

    @PostMapping("/añadir-blog")
    public ResponseEntity<Object> añadirBlogDelUsuario(@RequestBody Blog blogRequest) {

        Alert alerta = iBlogService.añadirBlog(blogRequest);
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

    @GetMapping("/{email}")
    public ResponseEntity<?> obtenerBlogsDelUsuario(@PathVariable("email") String email) {
        return iBlogService.obtenerListaDeBlogs(email);
    }

    @GetMapping("/list-blogs")
    public ResponseEntity<?> obtenerTodosLosBlogs() {
        return iBlogService.buscarTodosLosBlogs();
    }
}

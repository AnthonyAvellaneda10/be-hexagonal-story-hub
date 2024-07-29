package com.uni.pe.storyhub.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uni.pe.storyhub.model.*;
import com.uni.pe.storyhub.service.IBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> obtenerBlogsDelUsuario(
            @PathVariable("email") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return iBlogService.obtenerListaDeBlogs(email, page, size);
    }

    @GetMapping("/list-blogs")
    public ResponseEntity<?> obtenerTodosLosBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return iBlogService.buscarTodosLosBlogs(PageRequest.of(page, size));
    }

    @GetMapping("/list-tags")
    public ResponseEntity<?> obtenerTodosLosTags() {
        return iBlogService.obtenerTagsCreados();
    }

    @GetMapping("/detalle/{slug}/{idUsuario}")
    public ResponseEntity<?> obtenerDetalleDelBlog(@PathVariable("slug") String slug, @PathVariable("idUsuario") Integer idUsuario) {
        return iBlogService.obtenerInformacionDelBlog(slug, idUsuario);
    }

    @PostMapping("/dar-like")
    public ResponseEntity<?> darLikeAlBlog(@RequestBody LikeRequest likeRequest) {
        //System.out.println("likeRequest: " + likeRequest.getIdUsuario() + " " + likeRequest.getIdBlog());
        return iBlogService.darLikeAlBlog(likeRequest.getId_usuario(), likeRequest.getId_blog());
    }

    @PostMapping("/verificar-like")
    public ResponseEntity<?> verificarLike(@RequestBody LikeRequest likeRequest) {
        //System.out.println("likeRequest: " + likeRequest.getIdUsuario() + " " + likeRequest.getIdBlog());
        return iBlogService.verificarLike(likeRequest.getId_usuario(), likeRequest.getId_blog());
    }

    @PutMapping("update-blog")
    public ResponseEntity<?> actualizarBlog(@RequestBody ObjectNode payload) {
        Integer idBlog = payload.get("id_blog").asInt();
        String breveDescripcion = payload.get("breve_descripcion").toString();
        String contenidoBlog = payload.get("contenido_blog").toString();
        Boolean publico = payload.get("publicado").asBoolean();

        return iBlogService.editarBlog(
                idBlog,
                breveDescripcion.substring(1, breveDescripcion.length() - 1),
                contenidoBlog.substring(1, contenidoBlog.length() - 1),
                publico
        );
    }

    @PutMapping("delete-blog")
    public ResponseEntity<?> eliminarBlog(@RequestBody Integer idBlog) {
        System.out.println(idBlog);
        return iBlogService.eliminarBlog(idBlog);
    }
}

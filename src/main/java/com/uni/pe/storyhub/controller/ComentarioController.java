package com.uni.pe.storyhub.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uni.pe.storyhub.service.IComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comentario")
@RequiredArgsConstructor
@CrossOrigin(origins = { "*" })
public class ComentarioController {
    @Autowired
    private IComentarioService iComentarioService;

    @GetMapping("/obtenerComentariosBlog/{id_blog}")
    public ResponseEntity<?> obtenerComentariosBlog(@PathVariable int id_blog) {
        return iComentarioService.obtenerComentariosBlog(id_blog);
    }

    @PostMapping("/publicarComentario")
    public ResponseEntity<Map<String, String>> publicarComentario(@RequestBody ObjectNode payload) {
        System.out.println("Payload: " + payload);
        String comentario = payload.get("comentario").toString();

        // Obtener el valor de reply_to y manejar null o string
        String replyTo = payload.get("reply_to").isNull() ? null : payload.get("reply_to").asText();

        return iComentarioService.publicarComentario(
                comentario.substring(1, comentario.length() - 1),
                payload.get("parent_comentario_id").asInt() == 0 ? null : payload.get("parent_comentario_id").asInt(),
                replyTo,
                payload.get("id_usuario").asInt(),
                payload.get("id_blog").asInt()
        );
    }

    @PostMapping("/editarComentario")
    public ResponseEntity<Map<String, String>> editarComentario(@RequestBody ObjectNode payload) {
        String comentario = payload.get("comentario").toString();
        return iComentarioService.editarComentario(
                comentario.substring(1, comentario.length() - 1),
                payload.get("id_comentario").asInt()
        );
    }

    @PostMapping("/eliminarComentario")
    public ResponseEntity<Map<String, String>> eliminarComentario(@RequestBody ObjectNode payload) {
        return iComentarioService.eliminarComentario(
                payload.get("id_comentario").asInt()
        );
    }
}

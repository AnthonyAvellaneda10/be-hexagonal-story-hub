package com.uni.pe.storyhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ComentarioBlog {
    private Integer id_usuario;
    private String email;
    private String nombre_completo;
    private String imagen_perfil;
    private String username;
    private Timestamp fecha_creacion;
    private String comentario;
    private Integer score;
    private String reply_to;
    private Integer parent_comentario_id;
    private List<Integer> replies;
    private Integer id_comentario;
}

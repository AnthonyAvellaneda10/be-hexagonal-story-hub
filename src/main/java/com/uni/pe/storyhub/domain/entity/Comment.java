package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Integer idComentario;
    private String comentario;

    @Builder.Default
    private Integer score = 0;
    private String replyTo;

    @Builder.Default
    private boolean deleted = false;
    private Blog blog;
    private User user;
    private Comment parent;

    @Builder.Default
    private List<Comment> replies = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

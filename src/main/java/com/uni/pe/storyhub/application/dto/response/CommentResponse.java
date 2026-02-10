package com.uni.pe.storyhub.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Integer idComentario;
    private String comentario;
    private Integer score;
    private UserResponse user;
    private String replyTo;
    private boolean deleted;
    private String parentComentarioId;
    private LocalDateTime fechaCreacion;
    private List<CommentResponse> replies;
}

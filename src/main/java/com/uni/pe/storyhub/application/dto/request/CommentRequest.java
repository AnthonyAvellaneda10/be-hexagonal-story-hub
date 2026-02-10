package com.uni.pe.storyhub.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;

    private Integer parentComentarioId;
    private String replyTo;
}

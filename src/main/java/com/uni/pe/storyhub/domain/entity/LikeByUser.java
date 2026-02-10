package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeByUser {
    private Integer idLikebyuser;
    private User user;
    private Blog blog;
    private boolean isLike;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

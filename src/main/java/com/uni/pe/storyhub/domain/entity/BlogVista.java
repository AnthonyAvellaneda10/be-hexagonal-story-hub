package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogVista {
    private Integer idVista;
    private Blog blog;
    private User user;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime fechaVista;
}

package com.uni.pe.storyhub.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private Integer idSession;
    private User user;
    private String refreshToken;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime expiraAt;
    private LocalDateTime createdAt;
}

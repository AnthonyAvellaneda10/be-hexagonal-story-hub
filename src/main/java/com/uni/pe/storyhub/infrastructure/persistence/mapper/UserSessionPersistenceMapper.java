package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.UserSession;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserSessionEntity;

public class UserSessionPersistenceMapper {
    public static UserSession toDomain(UserSessionEntity entity) {
        if (entity == null)
            return null;
        return UserSession.builder()
                .idSession(entity.getIdSession())
                .user(UserPersistenceMapper.toDomain(entity.getUser()))
                .refreshToken(entity.getRefreshToken())
                .userAgent(entity.getUserAgent())
                .ipAddress(entity.getIpAddress())
                .expiraAt(entity.getExpiraAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static UserSessionEntity toEntity(UserSession domain) {
        if (domain == null)
            return null;
        return UserSessionEntity.builder()
                .idSession(domain.getIdSession())
                .user(UserPersistenceMapper.toEntity(domain.getUser()))
                .refreshToken(domain.getRefreshToken())
                .userAgent(domain.getUserAgent())
                .ipAddress(domain.getIpAddress())
                .expiraAt(domain.getExpiraAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

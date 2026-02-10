package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.BlogVista;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogVistaEntity;

public class BlogVistaPersistenceMapper {
    public static BlogVista toDomain(BlogVistaEntity entity) {
        if (entity == null)
            return null;
        return BlogVista.builder()
                .idVista(entity.getIdVista())
                .blog(BlogPersistenceMapper.toDomain(entity.getBlog()))
                .user(UserPersistenceMapper.toDomain(entity.getUser()))
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .fechaVista(entity.getFechaVista())
                .build();
    }

    public static BlogVistaEntity toEntity(BlogVista domain) {
        if (domain == null)
            return null;
        return BlogVistaEntity.builder()
                .idVista(domain.getIdVista())
                .blog(BlogPersistenceMapper.toEntity(domain.getBlog()))
                .user(UserPersistenceMapper.toEntity(domain.getUser()))
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .fechaVista(domain.getFechaVista())
                .build();
    }
}

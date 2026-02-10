package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.LikeByUser;
import com.uni.pe.storyhub.infrastructure.persistence.entity.LikeByUserEntity;

public class LikeByUserPersistenceMapper {
    public static LikeByUser toDomain(LikeByUserEntity entity) {
        if (entity == null)
            return null;
        return LikeByUser.builder()
                .idLikebyuser(entity.getIdLikebyuser())
                .user(UserPersistenceMapper.toDomain(entity.getUser()))
                .blog(BlogPersistenceMapper.toDomain(entity.getBlog()))
                .isLike(entity.isLike())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public static LikeByUserEntity toEntity(LikeByUser domain) {
        if (domain == null)
            return null;
        return LikeByUserEntity.builder()
                .idLikebyuser(domain.getIdLikebyuser())
                .user(UserPersistenceMapper.toEntity(domain.getUser()))
                .blog(BlogPersistenceMapper.toEntity(domain.getBlog()))
                .isLike(domain.isLike())
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}

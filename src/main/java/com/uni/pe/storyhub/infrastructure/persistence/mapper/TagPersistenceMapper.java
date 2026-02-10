package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.Tag;
import com.uni.pe.storyhub.infrastructure.persistence.entity.TagEntity;

public class TagPersistenceMapper {

    public static Tag toDomain(TagEntity entity) {
        if (entity == null)
            return null;
        return Tag.builder()
                .idTag(entity.getIdTag())
                .nombre(entity.getNombre())
                .build();
    }

    public static TagEntity toEntity(Tag domain) {
        if (domain == null)
            return null;
        return TagEntity.builder()
                .idTag(domain.getIdTag())
                .nombre(domain.getNombre())
                .build();
    }
}

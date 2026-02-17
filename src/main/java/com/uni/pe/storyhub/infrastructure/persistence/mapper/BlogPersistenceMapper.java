package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import java.util.stream.Collectors;

public class BlogPersistenceMapper {

    public static Blog toDomain(BlogEntity entity) {
        if (entity == null)
            return null;
        return Blog.builder()
                .idBlog(entity.getIdBlog())
                .titulo(entity.getTitulo())
                .breveDescripcion(entity.getBreveDescripcion())
                .imgBanner(entity.getImgBanner())
                .imgPortada(entity.getImgPortada())
                .descripcionImgPortada(entity.getDescripcionImgPortada())
                .contenidoBlog(entity.getContenidoBlog())
                .publicado(entity.isPublicado())
                .likes(entity.getLikes())
                .vistasCount(entity.getVistasCount())
                .removed(entity.isRemoved())
                .slug(entity.getSlug())
                .author(UserPersistenceMapper.toDomain(entity.getAuthor()))
                .tags(entity.getTags().stream().map(TagPersistenceMapper::toDomain).collect(Collectors.toSet()))
                // Comments are handled on demand or skipped to avoid circular deps if needed
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .usuarioCreacion(entity.getUsuarioCreacion())
                .usuarioActualizacion(entity.getUsuarioActualizacion())
                .build();
    }

    public static BlogEntity toEntity(Blog domain) {
        if (domain == null)
            return null;
        return BlogEntity.builder()
                .idBlog(domain.getIdBlog())
                .titulo(domain.getTitulo())
                .breveDescripcion(domain.getBreveDescripcion())
                .imgBanner(domain.getImgBanner())
                .imgPortada(domain.getImgPortada())
                .descripcionImgPortada(domain.getDescripcionImgPortada())
                .contenidoBlog(domain.getContenidoBlog())
                .publicado(domain.isPublicado())
                .likes(domain.getLikes())
                .vistasCount(domain.getVistasCount())
                .removed(domain.isRemoved())
                .slug(domain.getSlug())
                .author(UserPersistenceMapper.toEntity(domain.getAuthor()))
                .tags(domain.getTags().stream().map(TagPersistenceMapper::toEntity).collect(Collectors.toSet()))
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .usuarioCreacion(domain.getUsuarioCreacion())
                .usuarioActualizacion(domain.getUsuarioActualizacion())
                .build();
    }
}

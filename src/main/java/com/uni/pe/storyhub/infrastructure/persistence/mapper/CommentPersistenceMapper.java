package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.infrastructure.persistence.entity.CommentEntity;
import java.util.stream.Collectors;

public class CommentPersistenceMapper {

    public static Comment toDomain(CommentEntity entity) {
        if (entity == null)
            return null;
        return Comment.builder()
                .idComentario(entity.getIdComentario())
                .comentario(entity.getComentario())
                .score(entity.getScore())
                .replyTo(entity.getReplyTo())
                .deleted(entity.isDeleted())
                // Relationships handled carefully
                .blog(BlogPersistenceMapper.toDomain(entity.getBlog()))
                .user(UserPersistenceMapper.toDomain(entity.getUser()))
                .parent(toDomainParent(entity.getParent()))
                .replies(entity.getReplies().stream().map(CommentPersistenceMapper::toDomain)
                        .collect(Collectors.toList()))
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private static Comment toDomainParent(CommentEntity entity) {
        if (entity == null)
            return null;
        return Comment.builder()
                .idComentario(entity.getIdComentario())
                .comentario(entity.getComentario())
                .build();
    }

    public static CommentEntity toEntity(Comment domain) {
        if (domain == null)
            return null;
        return CommentEntity.builder()
                .idComentario(domain.getIdComentario())
                .comentario(domain.getComentario())
                .score(domain.getScore())
                .replyTo(domain.getReplyTo())
                .deleted(domain.isDeleted())
                .blog(BlogPersistenceMapper.toEntity(domain.getBlog()))
                .user(UserPersistenceMapper.toEntity(domain.getUser()))
                // Parent/Replies handled to avoid deep recursion if not needed
                .fechaCreacion(domain.getFechaCreacion())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}

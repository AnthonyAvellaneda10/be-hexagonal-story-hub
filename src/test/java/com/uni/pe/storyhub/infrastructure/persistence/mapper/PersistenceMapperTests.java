package com.uni.pe.storyhub.infrastructure.persistence.mapper;

import com.uni.pe.storyhub.domain.entity.*;
import com.uni.pe.storyhub.infrastructure.persistence.entity.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceMapperTests {

    @Test
    void userMapper_Comprehensive() {
        assertNull(UserPersistenceMapper.toDomain(null));
        assertNull(UserPersistenceMapper.toEntity(null));

        UserEntity entity = UserEntity.builder().idUsuario(1).email("test@test.com").build();
        User domain = UserPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdUsuario());

        User domainInput = User.builder().idUsuario(1).email("test@test.com").build();
        UserEntity entityResult = UserPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdUsuario());
    }

    @Test
    void blogMapper_Comprehensive() {
        assertNull(BlogPersistenceMapper.toDomain(null));
        assertNull(BlogPersistenceMapper.toEntity(null));

        BlogEntity entity = BlogEntity.builder().idBlog(1).tags(new HashSet<>()).build();
        Blog domain = BlogPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdBlog());

        Blog domainInput = Blog.builder().idBlog(1).tags(new HashSet<>()).build();
        BlogEntity entityResult = BlogPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdBlog());
    }

    @Test
    void commentMapper_Comprehensive() {
        assertNull(CommentPersistenceMapper.toDomain(null));
        assertNull(CommentPersistenceMapper.toEntity(null));

        CommentEntity parentEntity = CommentEntity.builder().idComentario(3).comentario("parent").build();
        CommentEntity reply = CommentEntity.builder().idComentario(2).replies(new ArrayList<>()).build();
        CommentEntity entity = CommentEntity.builder()
                .idComentario(1)
                .parent(parentEntity)
                .replies(List.of(reply))
                .build();
        Comment domain = CommentPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdComentario());
        assertEquals(3, domain.getParent().getIdComentario());

        Comment domainInput = Comment.builder().idComentario(1).build();
        CommentEntity entityResult = CommentPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdComentario());
    }

    @Test
    void tagMapper_Comprehensive() {
        assertNull(TagPersistenceMapper.toDomain(null));
        assertNull(TagPersistenceMapper.toEntity(null));

        TagEntity entity = TagEntity.builder().idTag(1).nombre("java").build();
        Tag domain = TagPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdTag());

        Tag domainInput = Tag.builder().idTag(1).nombre("java").build();
        TagEntity entityResult = TagPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdTag());
    }

    @Test
    void userSessionMapper_Comprehensive() {
        assertNull(UserSessionPersistenceMapper.toDomain(null));
        assertNull(UserSessionPersistenceMapper.toEntity(null));

        UserSessionEntity entity = UserSessionEntity.builder().idSession(1).refreshToken("token").build();
        UserSession domain = UserSessionPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdSession());

        UserSession domainInput = UserSession.builder().idSession(1).refreshToken("token").build();
        UserSessionEntity entityResult = UserSessionPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdSession());
    }

    @Test
    void blogVistaMapper_Comprehensive() {
        assertNull(BlogVistaPersistenceMapper.toDomain(null));
        assertNull(BlogVistaPersistenceMapper.toEntity(null));

        BlogVistaEntity entity = BlogVistaEntity.builder().idVista(1).ipAddress("127.0.0.1").build();
        BlogVista domain = BlogVistaPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdVista());

        BlogVista domainInput = BlogVista.builder().idVista(1).ipAddress("127.0.0.1").build();
        BlogVistaEntity entityResult = BlogVistaPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdVista());
    }

    @Test
    void likeByUserMapper_Comprehensive() {
        assertNull(LikeByUserPersistenceMapper.toDomain(null));
        assertNull(LikeByUserPersistenceMapper.toEntity(null));

        LikeByUserEntity entity = LikeByUserEntity.builder().idLikebyuser(1).isLike(true).build();
        LikeByUser domain = LikeByUserPersistenceMapper.toDomain(entity);
        assertEquals(1, domain.getIdLikebyuser());

        LikeByUser domainInput = LikeByUser.builder().idLikebyuser(1).isLike(true).build();
        LikeByUserEntity entityResult = LikeByUserPersistenceMapper.toEntity(domainInput);
        assertEquals(1, entityResult.getIdLikebyuser());
    }
}

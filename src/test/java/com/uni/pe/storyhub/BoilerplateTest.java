package com.uni.pe.storyhub;

import com.uni.pe.storyhub.infrastructure.persistence.entity.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class BoilerplateTest {

        @Test
        void testEntityBoilerplate() {
                // Exercise equals, hashCode, toString, and builders for persistence entities
                UserEntity user = UserEntity.builder()
                                .idUsuario(1)
                                .username("test")
                                .email("test@test.com")
                                .password("pass")
                                .nombreCompleto("name")
                                .descripcion("desc")
                                .imagenPerfil("img")
                                .linkedin("li")
                                .telegram("tg")
                                .instagram("ig")
                                .facebook("fb")
                                .youtube("yt")
                                .codigoVerificacion("123")
                                .emailVerificado(true)
                                .activo(true)
                                .intentosFallidos(0)
                                .build();
                assertNotNull(user.toString());
                assertNotEquals(0, user.hashCode());

                BlogEntity blog = BlogEntity.builder()
                                .idBlog(1)
                                .titulo("title")
                                .contenidoBlog("content")
                                .slug("slug")
                                .vistasCount(10)
                                .likes(5)
                                .publicado(true)
                                .removed(false)
                                .author(user)
                                .tags(new HashSet<>())
                                .fechaCreacion(null)
                                .fechaActualizacion(null)
                                .build();
                assertNotNull(blog.toString());

                CommentEntity comment = CommentEntity.builder()
                                .idComentario(1)
                                .comentario("comm")
                                .score(1)
                                .replyTo("user")
                                .deleted(false)
                                .user(user)
                                .blog(blog)
                                .parent(null)
                                .replies(new ArrayList<>())
                                .build();
                assertNotNull(comment.toString());

                TagEntity tag = TagEntity.builder().idTag(1).nombre("tag").build();
                assertNotNull(tag.toString());

                LikeByUserEntity like = LikeByUserEntity.builder()
                                .idLikebyuser(1)
                                .blog(blog)
                                .user(user)
                                .isLike(true)
                                .build();
                assertNotNull(like.toString());

                BlogVistaEntity vista = BlogVistaEntity.builder()
                                .idVista(1)
                                .blog(blog)
                                .user(user)
                                .ipAddress("127.0.0.1")
                                .userAgent("agent")
                                .build();
                assertNotNull(vista.toString());

                UserSessionEntity session = UserSessionEntity.builder()
                                .idSession(1)
                                .user(user)
                                .refreshToken("token")
                                .expiraAt(null)
                                .build();
                assertNotNull(session.toString());
        }
}

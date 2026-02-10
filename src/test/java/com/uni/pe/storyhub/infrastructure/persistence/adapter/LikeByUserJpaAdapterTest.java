package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.LikeByUser;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.LikeByUserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaLikeByUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeByUserJpaAdapterTest {

    @Mock
    private JpaLikeByUserRepository jpaLikeByUserRepository;

    @InjectMocks
    private LikeByUserJpaAdapter likeByUserJpaAdapter;

    private User user;
    private Blog blog;
    private LikeByUser likeByUser;
    private LikeByUserEntity likeByUserEntity;

    @BeforeEach
    void setUp() {
        user = User.builder().idUsuario(1).build();
        blog = Blog.builder().idBlog(1).build();
        likeByUser = LikeByUser.builder().idLikebyuser(1).user(user).blog(blog).isLike(true).build();

        likeByUserEntity = LikeByUserEntity.builder()
                .idLikebyuser(1)
                .user(UserEntity.builder().idUsuario(1).build())
                .blog(BlogEntity.builder().idBlog(1).build())
                .isLike(true)
                .build();
    }

    @Test
    void findByUserAndBlog_Success() {
        when(jpaLikeByUserRepository.findByUser_IdUsuarioAndBlog_IdBlog(1, 1))
                .thenReturn(Optional.of(likeByUserEntity));
        Optional<LikeByUser> result = likeByUserJpaAdapter.findByUserAndBlog(user, blog);
        assertTrue(result.isPresent());
        verify(jpaLikeByUserRepository).findByUser_IdUsuarioAndBlog_IdBlog(1, 1);
    }

    @Test
    void countLikesByBlogId_Success() {
        when(jpaLikeByUserRepository.countByBlog_IdBlogAndIsLikeTrue(1)).thenReturn(10L);
        long result = likeByUserJpaAdapter.countLikesByBlogId(1);
        assertEquals(10L, result);
    }

    @Test
    void save_Success() {
        when(jpaLikeByUserRepository.save(any(LikeByUserEntity.class))).thenReturn(likeByUserEntity);
        LikeByUser result = likeByUserJpaAdapter.save(likeByUser);
        assertNotNull(result);
    }
}

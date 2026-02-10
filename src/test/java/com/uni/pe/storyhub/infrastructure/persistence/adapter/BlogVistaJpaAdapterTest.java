package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.BlogVista;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogVistaEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaBlogVistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogVistaJpaAdapterTest {

    @Mock
    private JpaBlogVistaRepository jpaBlogVistaRepository;

    @InjectMocks
    private BlogVistaJpaAdapter blogVistaJpaAdapter;

    private Blog blog;
    private User user;
    private BlogVista blogVista;
    private BlogVistaEntity blogVistaEntity;

    @BeforeEach
    void setUp() {
        blog = Blog.builder().idBlog(1).build();
        user = User.builder().idUsuario(1).build();
        blogVista = BlogVista.builder().idVista(1).blog(blog).user(user).build();

        blogVistaEntity = BlogVistaEntity.builder()
                .idVista(1)
                .blog(BlogEntity.builder().idBlog(1).build())
                .user(UserEntity.builder().idUsuario(1).build())
                .build();
    }

    @Test
    void countByBlogId_Success() {
        when(jpaBlogVistaRepository.countByBlog_IdBlog(1)).thenReturn(5L);
        long result = blogVistaJpaAdapter.countByBlogId(1);
        assertEquals(5L, result);
    }

    @Test
    void save_Success() {
        when(jpaBlogVistaRepository.save(any(BlogVistaEntity.class))).thenReturn(blogVistaEntity);
        BlogVista result = blogVistaJpaAdapter.save(blogVista);
        assertNotNull(result);
    }

    @Test
    void existsByBlogAndUser_Success() {
        when(jpaBlogVistaRepository.existsByBlog_IdBlogAndUser_IdUsuario(1, 1)).thenReturn(true);
        assertTrue(blogVistaJpaAdapter.existsByBlogAndUser(blog, user));
    }

    @Test
    void existsByBlogAndUserIsNullAndIpAddressAndUserAgent_Success() {
        when(jpaBlogVistaRepository.existsByBlog_IdBlogAndUserIsNullAndIpAddressAndUserAgent(1, "127.0.0.1", "agent"))
                .thenReturn(true);
        assertTrue(blogVistaJpaAdapter.existsByBlogAndUserIsNullAndIpAddressAndUserAgent(blog, "127.0.0.1", "agent"));
    }
}

package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaBlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogJpaAdapterTest {

    @Mock
    private JpaBlogRepository jpaBlogRepository;

    @InjectMocks
    private BlogJpaAdapter blogJpaAdapter;

    private Blog blog;
    private BlogEntity blogEntity;
    private UserEntity authorEntity;

    @BeforeEach
    void setUp() {
        authorEntity = UserEntity.builder().idUsuario(1).build();

        blog = Blog.builder()
                .idBlog(1)
                .slug("test-blog")
                .author(com.uni.pe.storyhub.domain.entity.User.builder().idUsuario(1).build())
                .tags(new HashSet<>())
                .build();

        blogEntity = BlogEntity.builder()
                .idBlog(1)
                .slug("test-blog")
                .author(authorEntity)
                .tags(new HashSet<>())
                .build();
    }

    @Test
    void findById_Success() {
        when(jpaBlogRepository.findById(1)).thenReturn(Optional.of(blogEntity));
        Optional<Blog> result = blogJpaAdapter.findById(1);
        assertTrue(result.isPresent());
        verify(jpaBlogRepository).findById(1);
    }

    @Test
    void save_Success() {
        when(jpaBlogRepository.save(any(BlogEntity.class))).thenReturn(blogEntity);
        Blog result = blogJpaAdapter.save(blog);
        assertNotNull(result);
        verify(jpaBlogRepository).save(any(BlogEntity.class));
    }

    @Test
    void deleteById_Success() {
        doNothing().when(jpaBlogRepository).deleteById(1);
        blogJpaAdapter.deleteById(1);
        verify(jpaBlogRepository).deleteById(1);
    }

    @Test
    void findBySlugAndRemovedFalse_Success() {
        when(jpaBlogRepository.findBySlugAndRemovedFalse("test-blog")).thenReturn(Optional.of(blogEntity));
        Optional<Blog> result = blogJpaAdapter.findBySlugAndRemovedFalse("test-blog");
        assertTrue(result.isPresent());
    }

    @Test
    void existsBySlugAndRemovedFalse_Success() {
        when(jpaBlogRepository.existsBySlugAndRemovedFalse("test-blog")).thenReturn(true);
        assertTrue(blogJpaAdapter.existsBySlugAndRemovedFalse("test-blog"));
    }

    @Test
    void findByAuthorEmailAndRemovedFalse_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogEntity> page = new PageImpl<>(Collections.singletonList(blogEntity));
        when(jpaBlogRepository.findByAuthor_EmailAndRemovedFalse(anyString(), any())).thenReturn(page);

        Page<Blog> result = blogJpaAdapter.findByAuthorEmailAndRemovedFalse("test@test.com", pageable);

        assertFalse(result.isEmpty());
        verify(jpaBlogRepository).findByAuthor_EmailAndRemovedFalse("test@test.com", pageable);
    }

    @Test
    void findByPublicadoTrueAndRemovedFalse_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogEntity> page = new PageImpl<>(Collections.singletonList(blogEntity));
        when(jpaBlogRepository.findByPublicadoTrueAndRemovedFalse(any())).thenReturn(page);

        Page<Blog> result = blogJpaAdapter.findByPublicadoTrueAndRemovedFalse(pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByAuthorUsernameAndPublicadoTrueAndRemovedFalse_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogEntity> page = new PageImpl<>(Collections.singletonList(blogEntity));
        when(jpaBlogRepository.findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(anyString(), any()))
                .thenReturn(page);

        Page<Blog> result = blogJpaAdapter.findByAuthorUsernameAndPublicadoTrueAndRemovedFalse("user", pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByAuthorIdAndRemovedFalse_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogEntity> page = new PageImpl<>(Collections.singletonList(blogEntity));
        when(jpaBlogRepository.findByAuthor_IdUsuarioAndRemovedFalse(anyInt(), any())).thenReturn(page);

        Page<Blog> result = blogJpaAdapter.findByAuthorIdAndRemovedFalse(1, pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findByTagName_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogEntity> page = new PageImpl<>(Collections.singletonList(blogEntity));
        when(jpaBlogRepository.findByPublicadoTrueAndRemovedFalseAndTags_Nombre(anyString(), any())).thenReturn(page);

        Page<Blog> result = blogJpaAdapter.findByTagName("tag", pageable);

        assertFalse(result.isEmpty());
    }
}

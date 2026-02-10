package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.CommentEntity;
import com.uni.pe.storyhub.infrastructure.persistence.entity.UserEntity;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaCommentRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentJpaAdapterTest {

    @Mock
    private JpaCommentRepository jpaCommentRepository;

    @InjectMocks
    private CommentJpaAdapter commentJpaAdapter;

    private Comment comment;
    private CommentEntity commentEntity;
    private BlogEntity blogEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder().idUsuario(1).build();
        blogEntity = BlogEntity.builder().idBlog(1).build();

        comment = Comment.builder()
                .idComentario(1)
                .comentario("Test comment")
                .blog(com.uni.pe.storyhub.domain.entity.Blog.builder().idBlog(1).build())
                .user(com.uni.pe.storyhub.domain.entity.User.builder().idUsuario(1).build())
                .replies(new ArrayList<>())
                .build();

        commentEntity = CommentEntity.builder()
                .idComentario(1)
                .comentario("Test comment")
                .blog(blogEntity)
                .user(userEntity)
                .replies(new ArrayList<>())
                .build();
    }

    @Test
    void findById_Success() {
        when(jpaCommentRepository.findById(1)).thenReturn(Optional.of(commentEntity));
        Optional<Comment> result = commentJpaAdapter.findById(1);
        assertTrue(result.isPresent());
    }

    @Test
    void save_Success() {
        when(jpaCommentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);
        Comment result = commentJpaAdapter.save(comment);
        assertNotNull(result);
    }

    @Test
    void deleteById_Success() {
        doNothing().when(jpaCommentRepository).deleteById(1);
        commentJpaAdapter.deleteById(1);
        verify(jpaCommentRepository).deleteById(1);
    }

    @Test
    void findParentCommentsByBlogId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentEntity> page = new PageImpl<>(Collections.singletonList(commentEntity));
        when(jpaCommentRepository.findByBlog_IdBlogAndParentIsNullAndDeletedFalseOrderByIdComentarioAsc(anyInt(),
                any()))
                .thenReturn(page);

        Page<Comment> result = commentJpaAdapter.findParentCommentsByBlogId(1, pageable);

        assertFalse(result.isEmpty());
    }

    @Test
    void findAll_Success() {
        when(jpaCommentRepository.findAll()).thenReturn(Collections.singletonList(commentEntity));
        List<Comment> result = commentJpaAdapter.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}

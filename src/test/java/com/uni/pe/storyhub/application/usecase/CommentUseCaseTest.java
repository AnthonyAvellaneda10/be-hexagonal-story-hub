package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateCommentRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.application.mapper.CommentMapper;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.domain.repository.CommentRepository;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentUseCaseTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ToastIdGenerator toastIdGenerator;

    @InjectMocks
    private CommentUseCase commentService;

    private User user;
    private Blog blog;
    private Comment comment;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUsuario(1);
        user.setEmail("test@test.com");

        blog = new Blog();
        blog.setIdBlog(1);
        blog.setPublicado(true);
        blog.setRemoved(false);

        commentRequest = new CommentRequest();
        commentRequest.setComentario("Test comment");

        comment = new Comment();
        comment.setIdComentario(1);
        comment.setComentario("Test comment");
        comment.setUser(user);
        comment.setBlog(blog);
        comment.setScore(0);
    }

    @Test
    void addComment_Success() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<CommentResponse> response = commentService.addComment(1, commentRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_WithReply_Success() {
        commentRequest.setParentComentarioId(1);
        Comment parent = new Comment();
        parent.setIdComentario(1);

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(parent));
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<CommentResponse> response = commentService.addComment(1, commentRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ParentNotFound() {
        commentRequest.setParentComentarioId(99);
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(any())).thenReturn(comment); // Fix NPE
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.addComment(1, commentRequest, "test@test.com"));
        assertEquals("Comentario padre no encontrado", exception.getMessage());
    }

    @Test
    void updateComment_Success() {
        UpdateCommentRequest updateRequest = UpdateCommentRequest.builder()
                .comentario("Updated content")
                .build();

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<CommentResponse> response = commentService.updateComment(1, updateRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        verify(commentRepository).save(comment);
        assertEquals("Updated content", comment.getComentario());
    }

    @Test
    void updateComment_NotOwner() {
        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        assertThrows(BusinessException.class,
                () -> commentService.updateComment(1, updateRequest, "other@test.com"));
    }

    @Test
    void updateComment_NotFound() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
        assertThrows(BusinessException.class,
                () -> commentService.updateComment(1, updateRequest, "test@test.com"));
    }

    @Test
    void updateComment_WithParent_Success() {
        Comment parent = new Comment();
        parent.setIdComentario(2);
        UpdateCommentRequest updateRequest = UpdateCommentRequest.builder()
                .parentComentarioId(2)
                .replyTo("anotherUser")
                .build();

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(commentRepository.findById(2)).thenReturn(Optional.of(parent));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<CommentResponse> response = commentService.updateComment(1, updateRequest, "test@test.com");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals(parent, comment.getParent());
        assertEquals("anotherUser", comment.getReplyTo());
    }

    @Test
    void updateComment_ParentNotFound() {
        UpdateCommentRequest updateRequest = UpdateCommentRequest.builder()
                .parentComentarioId(99)
                .build();

        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(commentRepository.findById(99)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.updateComment(1, updateRequest, "test@test.com"));
        assertEquals("Comentario padre no encontrado", exception.getMessage());
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = commentService.deleteComment(1, "test@test.com");

        assertNotNull(response);
        assertTrue(comment.isDeleted());
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_AlreadyDeleted() {
        comment.setDeleted(true);
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.deleteComment(1, "test@test.com"));
        assertEquals("Comentario no encontrado", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void deleteComment_CommentNotFound() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> commentService.deleteComment(1, "test@test.com"));
    }

    @Test
    void deleteComment_NotOwner() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        assertThrows(BusinessException.class, () -> commentService.deleteComment(1, "other@test.com"));
    }

    @Test
    void getCommentsByBlog_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment));

        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(commentRepository.findParentCommentsByBlogId(anyInt(),
                any(Pageable.class)))
                .thenReturn(commentPage);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Page<CommentResponse>> response = commentService.getCommentsByBlog(1, pageable);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
    }

    @Test
    @DisplayName("getCommentsByBlog - Blog not found (404)")
    void getCommentsByBlog_BlogNotFound() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.empty());

        PageRequest pageRequest = PageRequest.of(0, 10);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.getCommentsByBlog(1, pageRequest));
        assertEquals("Blog no encontrado", exception.getMessage());
        assertEquals(404, exception.getStatusCode());
    }

    @Test
    @DisplayName("getCommentsByBlog - Blog not published (403)")
    void getCommentsByBlog_BlogNotPublished() {
        blog.setPublicado(false);
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));

        PageRequest pageRequest = PageRequest.of(0, 10);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.getCommentsByBlog(1, pageRequest));
        assertEquals("Este blog aún no se encuentra disponible de manera pública.", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    @DisplayName("getCommentsByBlog - Blog removed (403)")
    void getCommentsByBlog_BlogRemoved() {
        blog.setRemoved(true);
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));

        PageRequest pageRequest = PageRequest.of(0, 10);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.getCommentsByBlog(1, pageRequest));
        assertEquals("Este blog ha sido eliminado.", exception.getMessage());
        assertEquals(403, exception.getStatusCode());
    }

    @Test
    void upvoteComment_Success() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = commentService.upvoteComment(1);

        assertNotNull(response);
        assertEquals(1, comment.getScore());
        verify(commentRepository).save(comment);
    }

    @Test
    void upvoteComment_CommentNotFound() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> commentService.upvoteComment(1));
    }

    @Test
    void getCommentsByBlog_Empty_Success() {
        when(blogRepository.findById(anyInt())).thenReturn(Optional.of(blog));
        when(commentRepository.findParentCommentsByBlogId(anyInt(), any(Pageable.class))).thenReturn(Page.empty());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Page<CommentResponse>> response = commentService.getCommentsByBlog(1, PageRequest.of(0, 10));

        assertNotNull(response);
        assertTrue(response.getData().isEmpty());
    }
}

package com.uni.pe.storyhub.application.service.impl;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateCommentRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.application.mapper.CommentMapper;
import com.uni.pe.storyhub.application.service.CommentService;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.domain.repository.CommentRepository;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

        private final CommentRepository commentRepository;
        private final BlogRepository blogRepository;
        private final UserRepository userRepository;
        private final CommentMapper commentMapper;
        private final ToastIdGenerator toastIdGenerator;

        private static final String BLOG_NOT_FOUND = "Blog no encontrado";
        private static final String COMMENT_NOT_FOUND = "Comentario no encontrado";
        private static final String USER_NOT_FOUND = "Usuario no encontrado";
        private static final String SUCCESS_TYPE = ApiResponse.TYPE_SUCCESS;

        @Override
        @Transactional
        public ApiResponse<CommentResponse> addComment(Integer idBlog, CommentRequest request, String userEmail) {
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));
                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

                Comment comment = commentMapper.toEntity(request);
                comment.setBlog(blog);
                comment.setUser(user);
                comment.setScore(0);

                if (request.getParentComentarioId() != null) {
                        Comment parent = commentRepository.findById(request.getParentComentarioId())
                                        .orElseThrow(() -> new BusinessException("Comentario padre no encontrado", 0,
                                                        404));
                        comment.setParent(parent);
                }

                Comment savedComment = commentRepository.save(comment);
                return ApiResponse.<CommentResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Comentario añadido")
                                .type(SUCCESS_TYPE)
                                .statusCode(201)
                                .data(commentMapper.toResponse(savedComment))
                                .build();
        }

        @Override
        public ApiResponse<Page<CommentResponse>> getCommentsByBlog(Integer idBlog, Pageable pageable) {
                // Validate blog exists
                Blog blog = blogRepository.findById(idBlog)
                                .orElseThrow(() -> new BusinessException(BLOG_NOT_FOUND, 0, 404));

                // Validate blog status
                if (blog.isRemoved()) {
                        throw new BusinessException("Este blog ha sido eliminado.", 0, 403);
                }

                if (!blog.isPublicado()) {
                        throw new BusinessException("Este blog aún no se encuentra disponible de manera pública.", 0,
                                        403);
                }

                Page<Comment> topLevelComments = commentRepository
                                .findByBlog_IdBlogAndParentIsNullAndDeletedFalseOrderByIdComentarioAsc(idBlog,
                                                pageable);

                Page<CommentResponse> response = topLevelComments.map(commentMapper::toResponse);

                return ApiResponse.<Page<CommentResponse>>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Comentarios recuperados")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(response)
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<CommentResponse> updateComment(Integer id, UpdateCommentRequest request, String userEmail) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new BusinessException(COMMENT_NOT_FOUND, 0, 404));

                if (!comment.getUser().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para editar este comentario", 0, 403);
                }

                if (request.getComentario() != null)
                        comment.setComentario(request.getComentario());
                if (request.getReplyTo() != null)
                        comment.setReplyTo(request.getReplyTo());
                if (request.getParentComentarioId() != null) {
                        Comment parent = commentRepository.findById(request.getParentComentarioId())
                                        .orElseThrow(() -> new BusinessException("Comentario padre no encontrado", 0,
                                                        404));
                        comment.setParent(parent);
                }

                Comment updatedComment = commentRepository.save(comment);

                return ApiResponse.<CommentResponse>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Comentario actualizado")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .data(commentMapper.toResponse(updatedComment))
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<Void> deleteComment(Integer idComentario, String userEmail) {
                Comment comment = commentRepository.findById(idComentario)
                                .orElseThrow(() -> new BusinessException(COMMENT_NOT_FOUND, 0, 404));

                if (comment.isDeleted()) {
                        throw new BusinessException(COMMENT_NOT_FOUND, 0, 404);
                }

                if (!comment.getUser().getEmail().equals(userEmail)) {
                        throw new BusinessException("No tienes permiso para eliminar este comentario", 0, 403);
                }

                comment.setDeleted(true);
                commentRepository.save(comment);

                return ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Comentario eliminado")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .build();
        }

        @Override
        @Transactional
        public ApiResponse<Void> upvoteComment(Integer idComentario) {
                Comment comment = commentRepository.findById(idComentario)
                                .orElseThrow(() -> new BusinessException(COMMENT_NOT_FOUND, 0, 404));

                comment.setScore(comment.getScore() + 1);
                commentRepository.save(comment);

                return ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Upvote registrado")
                                .type(SUCCESS_TYPE)
                                .statusCode(200)
                                .build();
        }
}

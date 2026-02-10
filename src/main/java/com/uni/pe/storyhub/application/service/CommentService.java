package com.uni.pe.storyhub.application.service;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateCommentRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;

public interface CommentService {
    ApiResponse<CommentResponse> addComment(Integer idBlog, CommentRequest request, String userEmail);

    ApiResponse<org.springframework.data.domain.Page<CommentResponse>> getCommentsByBlog(Integer idBlog,
            org.springframework.data.domain.Pageable pageable);

    ApiResponse<CommentResponse> updateComment(Integer id, UpdateCommentRequest request, String userEmail);

    ApiResponse<Void> deleteComment(Integer idComentario, String userEmail);

    ApiResponse<Void> upvoteComment(Integer idComentario);
}

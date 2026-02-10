package com.uni.pe.storyhub.infrastructure.controller;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateCommentRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.application.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/blog/{idBlog}")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable Integer idBlog,
            @Valid @RequestBody CommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<CommentResponse> response = commentService.addComment(idBlog, request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/blog/{idBlog}")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CommentResponse>>> getCommentsByBlog(
            @PathVariable Integer idBlog,
            org.springframework.data.domain.Pageable pageable) {
        ApiResponse<org.springframework.data.domain.Page<CommentResponse>> response = commentService
                .getCommentsByBlog(idBlog, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<CommentResponse> response = commentService.updateComment(id, request, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Void> response = commentService.deleteComment(id, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<ApiResponse<Void>> upvoteComment(@PathVariable Integer id) {
        ApiResponse<Void> response = commentService.upvoteComment(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

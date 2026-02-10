package com.uni.pe.storyhub.infrastructure.controller;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateBlogRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.application.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(@Valid @RequestBody BlogRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<BlogResponse> response = blogService.createBlog(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(@PathVariable Integer id,
            @Valid @RequestBody UpdateBlogRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<BlogResponse> response = blogService.updateBlog(id, request, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Void> response = blogService.deleteBlog(id, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogBySlug(@PathVariable String slug,
            HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        ApiResponse<BlogResponse> response = blogService.getBlogBySlug(slug, ipAddress, userAgent, userEmail);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BlogResponse>>> getAllPublicBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Page<BlogResponse>> response = blogService.getAllPublicBlogs(PageRequest.of(page, size), userEmail);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<BlogResponse>>> getMyBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Page<BlogResponse>> response = blogService.getBlogsByUser(email, PageRequest.of(page, size), email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        ApiResponse<List<TagResponse>> response = blogService.getAllTags();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> giveLike(@PathVariable Integer id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Void> response = blogService.giveLike(id, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

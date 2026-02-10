package com.uni.pe.storyhub.application.port.in;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateBlogRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    ApiResponse<BlogResponse> createBlog(BlogRequest request, String userEmail);

    ApiResponse<BlogResponse> updateBlog(Integer idBlog, UpdateBlogRequest request, String userEmail);

    ApiResponse<Void> deleteBlog(Integer idBlog, String userEmail);

    ApiResponse<BlogResponse> getBlogBySlug(String slug, String ipAddress, String userAgent, String userEmail);

    ApiResponse<Page<BlogResponse>> getAllPublicBlogs(Pageable pageable, String userEmail);

    ApiResponse<Page<BlogResponse>> getBlogsByUser(String email, Pageable pageable, String currentUserEmail);

    ApiResponse<List<TagResponse>> getAllTags();

    ApiResponse<Void> giveLike(Integer idBlog, String userEmail);
}

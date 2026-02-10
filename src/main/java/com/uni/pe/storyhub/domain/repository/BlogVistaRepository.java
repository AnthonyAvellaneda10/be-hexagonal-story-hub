package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.BlogVista;
import com.uni.pe.storyhub.domain.entity.User;

public interface BlogVistaRepository {
    long countByBlogId(Integer idBlog);

    BlogVista save(BlogVista blogVista);

    boolean existsByBlogAndUser(Blog blog, User user);

    boolean existsByBlogAndUserIsNullAndIpAddressAndUserAgent(Blog blog, String ipAddress, String userAgent);
}

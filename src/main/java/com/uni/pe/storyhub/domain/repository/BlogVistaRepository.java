package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.BlogVista;
import com.uni.pe.storyhub.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogVistaRepository extends JpaRepository<BlogVista, Integer> {
    boolean existsByBlogAndUser(Blog blog, User user);

    boolean existsByBlogAndUserIsNullAndIpAddressAndUserAgent(Blog blog, String ipAddress, String userAgent);

    long countByBlog(Blog blog);
}

package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogVistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBlogVistaRepository extends JpaRepository<BlogVistaEntity, Integer> {
    long countByBlog_IdBlog(Integer idBlog);

    boolean existsByBlog_IdBlogAndUser_IdUsuario(Integer idBlog, Integer idUsuario);

    boolean existsByBlog_IdBlogAndUserIsNullAndIpAddressAndUserAgent(Integer idBlog, String ipAddress,
            String userAgent);
}

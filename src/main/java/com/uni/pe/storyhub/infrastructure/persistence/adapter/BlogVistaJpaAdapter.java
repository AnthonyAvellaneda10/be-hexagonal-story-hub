package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.BlogVista;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.repository.BlogVistaRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.BlogVistaPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaBlogVistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogVistaJpaAdapter implements BlogVistaRepository {

    private final JpaBlogVistaRepository jpaBlogVistaRepository;

    @Override
    public long countByBlogId(Integer idBlog) {
        return jpaBlogVistaRepository.countByBlog_IdBlog(idBlog);
    }

    @Override
    public BlogVista save(BlogVista blogVista) {
        return BlogVistaPersistenceMapper.toDomain(
                jpaBlogVistaRepository.save(BlogVistaPersistenceMapper.toEntity(blogVista)));
    }

    @Override
    public boolean existsByBlogAndUser(Blog blog, User user) {
        return jpaBlogVistaRepository.existsByBlog_IdBlogAndUser_IdUsuario(blog.getIdBlog(), user.getIdUsuario());
    }

    @Override
    public boolean existsByBlogAndUserIsNullAndIpAddressAndUserAgent(Blog blog, String ipAddress, String userAgent) {
        return jpaBlogVistaRepository.existsByBlog_IdBlogAndUserIsNullAndIpAddressAndUserAgent(blog.getIdBlog(),
                ipAddress,
                userAgent);
    }
}

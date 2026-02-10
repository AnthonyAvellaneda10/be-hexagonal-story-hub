package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.repository.BlogRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.BlogPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaBlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BlogJpaAdapter implements BlogRepository {

    private final JpaBlogRepository jpaBlogRepository;

    @Override
    public Optional<Blog> findById(Integer id) {
        return jpaBlogRepository.findById(id).map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public Blog save(Blog blog) {
        return BlogPersistenceMapper.toDomain(jpaBlogRepository.save(BlogPersistenceMapper.toEntity(blog)));
    }

    @Override
    public void deleteById(Integer id) {
        jpaBlogRepository.deleteById(id);
    }

    @Override
    public Optional<Blog> findBySlugAndRemovedFalse(String slug) {
        return jpaBlogRepository.findBySlugAndRemovedFalse(slug).map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsBySlugAndRemovedFalse(String slug) {
        return jpaBlogRepository.existsBySlugAndRemovedFalse(slug);
    }

    @Override
    public Page<Blog> findByAuthorEmailAndRemovedFalse(String email, Pageable pageable) {
        return jpaBlogRepository.findByAuthor_EmailAndRemovedFalse(email, pageable)
                .map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public Page<Blog> findByPublicadoTrueAndRemovedFalse(Pageable pageable) {
        return jpaBlogRepository.findByPublicadoTrueAndRemovedFalse(pageable).map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public Page<Blog> findByAuthorUsernameAndPublicadoTrueAndRemovedFalse(String username, Pageable pageable) {
        return jpaBlogRepository.findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(username, pageable)
                .map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public Page<Blog> findByAuthorIdAndRemovedFalse(Integer idUsuario, Pageable pageable) {
        return jpaBlogRepository.findByAuthor_IdUsuarioAndRemovedFalse(idUsuario, pageable)
                .map(BlogPersistenceMapper::toDomain);
    }

    @Override
    public Page<Blog> findByTagName(String tagName, Pageable pageable) {
        return jpaBlogRepository.findByPublicadoTrueAndRemovedFalseAndTags_Nombre(tagName, pageable)
                .map(BlogPersistenceMapper::toDomain);
    }
}

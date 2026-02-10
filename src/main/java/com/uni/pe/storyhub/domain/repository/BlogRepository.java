package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface BlogRepository {
    Page<Blog> findByPublicadoTrueAndRemovedFalse(Pageable pageable);

    Optional<Blog> findById(Integer id);

    Optional<Blog> findBySlugAndRemovedFalse(String slug);

    Page<Blog> findByAuthorIdAndRemovedFalse(Integer idUsuario, Pageable pageable);

    Page<Blog> findByTagName(String tagName, Pageable pageable);

    Blog save(Blog blog);

    void deleteById(Integer id);

    boolean existsBySlugAndRemovedFalse(String slug);

    Page<Blog> findByAuthorEmailAndRemovedFalse(String email, Pageable pageable);

    Page<Blog> findByAuthorUsernameAndPublicadoTrueAndRemovedFalse(String username, Pageable pageable);
}

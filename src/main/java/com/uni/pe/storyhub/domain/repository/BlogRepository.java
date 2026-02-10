package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Optional<Blog> findBySlugAndRemovedFalse(String slug);

    boolean existsBySlugAndRemovedFalse(String slug);

    Page<Blog> findByAuthor_EmailAndRemovedFalse(String email, Pageable pageable);

    Page<Blog> findByPublicadoTrueAndRemovedFalse(Pageable pageable);

    Page<Blog> findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(String username, Pageable pageable);
}

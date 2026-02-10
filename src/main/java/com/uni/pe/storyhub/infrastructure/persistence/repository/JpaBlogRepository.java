package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.BlogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaBlogRepository extends JpaRepository<BlogEntity, Integer> {
    Page<BlogEntity> findByPublicadoTrueAndRemovedFalse(Pageable pageable);

    Optional<BlogEntity> findBySlugAndRemovedFalse(String slug);

    Page<BlogEntity> findByAuthor_IdUsuarioAndRemovedFalse(Integer idUsuario, Pageable pageable);

    Page<BlogEntity> findByPublicadoTrueAndRemovedFalseAndTags_Nombre(String tagName, Pageable pageable);

    boolean existsBySlugAndRemovedFalse(String slug);

    Page<BlogEntity> findByAuthor_EmailAndRemovedFalse(String email, Pageable pageable);

    Page<BlogEntity> findByAuthor_UsernameAndPublicadoTrueAndRemovedFalse(String username, Pageable pageable);
}

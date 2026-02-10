package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.LikeByUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaLikeByUserRepository extends JpaRepository<LikeByUserEntity, Integer> {
    Optional<LikeByUserEntity> findByUser_IdUsuarioAndBlog_IdBlog(Integer idUsuario, Integer idBlog);

    long countByBlog_IdBlogAndIsLikeTrue(Integer idBlog);
}

package com.uni.pe.storyhub.infrastructure.persistence.repository;

import com.uni.pe.storyhub.infrastructure.persistence.entity.CommentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaCommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByBlog_IdBlogAndParentIsNullOrderByFechaCreacionDesc(Integer idBlog);

    Page<CommentEntity> findByBlog_IdBlogAndParentIsNullAndDeletedFalseOrderByIdComentarioAsc(Integer idBlog,
            Pageable pageable);
}

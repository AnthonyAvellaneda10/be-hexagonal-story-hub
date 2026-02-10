package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    org.springframework.data.domain.Page<Comment> findByBlog_IdBlogAndParentIsNullAndDeletedFalseOrderByIdComentarioAsc(
            Integer idBlog,
            org.springframework.data.domain.Pageable pageable);
}

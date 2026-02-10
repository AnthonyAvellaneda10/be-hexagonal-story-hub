package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Comment;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository {
    Optional<Comment> findById(Integer id);

    Comment save(Comment comment);

    void deleteById(Integer id);

    Page<Comment> findParentCommentsByBlogId(Integer idBlog, Pageable pageable);

    List<Comment> findAll();
}

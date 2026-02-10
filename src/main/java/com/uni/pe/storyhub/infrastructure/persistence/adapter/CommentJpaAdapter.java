package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.domain.repository.CommentRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.CommentPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentJpaAdapter implements CommentRepository {

    private final JpaCommentRepository jpaCommentRepository;

    @Override
    public Optional<Comment> findById(Integer id) {
        return jpaCommentRepository.findById(id).map(CommentPersistenceMapper::toDomain);
    }

    @Override
    public Comment save(Comment comment) {
        return CommentPersistenceMapper.toDomain(jpaCommentRepository.save(CommentPersistenceMapper.toEntity(comment)));
    }

    @Override
    public void deleteById(Integer id) {
        jpaCommentRepository.deleteById(id);
    }

    @Override
    public Page<Comment> findParentCommentsByBlogId(Integer idBlog, Pageable pageable) {
        return jpaCommentRepository
                .findByBlog_IdBlogAndParentIsNullAndDeletedFalseOrderByIdComentarioAsc(idBlog, pageable)
                .map(CommentPersistenceMapper::toDomain);
    }

    @Override
    public List<Comment> findAll() {
        return jpaCommentRepository.findAll().stream()
                .map(CommentPersistenceMapper::toDomain)
                .toList();
    }
}

package com.uni.pe.storyhub.infrastructure.persistence.adapter;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.entity.LikeByUser;
import com.uni.pe.storyhub.domain.repository.LikeByUserRepository;
import com.uni.pe.storyhub.infrastructure.persistence.mapper.LikeByUserPersistenceMapper;
import com.uni.pe.storyhub.infrastructure.persistence.repository.JpaLikeByUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeByUserJpaAdapter implements LikeByUserRepository {

    private final JpaLikeByUserRepository jpaLikeByUserRepository;

    @Override
    public Optional<LikeByUser> findByUserAndBlog(User user, Blog blog) {
        return jpaLikeByUserRepository.findByUser_IdUsuarioAndBlog_IdBlog(user.getIdUsuario(), blog.getIdBlog())
                .map(LikeByUserPersistenceMapper::toDomain);
    }

    @Override
    public long countLikesByBlogId(Integer idBlog) {
        return jpaLikeByUserRepository.countByBlog_IdBlogAndIsLikeTrue(idBlog);
    }

    @Override
    public LikeByUser save(LikeByUser likeByUser) {
        return LikeByUserPersistenceMapper.toDomain(
                jpaLikeByUserRepository.save(LikeByUserPersistenceMapper.toEntity(likeByUser)));
    }
}

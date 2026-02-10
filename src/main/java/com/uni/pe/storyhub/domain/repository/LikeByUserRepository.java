package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.entity.LikeByUser;
import java.util.Optional;

public interface LikeByUserRepository {
    Optional<LikeByUser> findByUserAndBlog(User user, Blog blog);

    long countLikesByBlogId(Integer idBlog);

    LikeByUser save(LikeByUser likeByUser);
}

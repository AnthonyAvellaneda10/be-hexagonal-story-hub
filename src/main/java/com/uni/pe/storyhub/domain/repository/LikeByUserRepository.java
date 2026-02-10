package com.uni.pe.storyhub.domain.repository;

import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.LikeByUser;
import com.uni.pe.storyhub.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeByUserRepository extends JpaRepository<LikeByUser, Integer> {
    Optional<LikeByUser> findByUserAndBlog(User user, Blog blog);

    long countByBlogAndIsLikeTrue(Blog blog);
}

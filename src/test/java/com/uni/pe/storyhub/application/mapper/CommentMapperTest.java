package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.domain.entity.Comment;
import com.uni.pe.storyhub.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void toEntity_ShouldMapCorrectly() {
        CommentRequest request = new CommentRequest();
        request.setComentario("Test comment");

        Comment entity = commentMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getComentario(), entity.getComentario());
    }

    @Test
    void toResponse_ShouldMapCorrectly() {
        User user = new User();
        user.setUsername("testuser");

        Comment comment = new Comment();
        comment.setIdComentario(1);
        comment.setComentario("Hello");
        comment.setScore(5);
        comment.setUser(user);

        CommentResponse response = commentMapper.toResponse(comment);

        assertNotNull(response);
        assertEquals(comment.getIdComentario(), response.getIdComentario());
        assertEquals(comment.getComentario(), response.getComentario());
        assertEquals(comment.getScore(), response.getScore());
        assertEquals(user.getUsername(), response.getUser().getUsername());
    }
}

package com.uni.pe.storyhub.application.mapper;

import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.domain.entity.Blog;
import com.uni.pe.storyhub.domain.entity.Tag;
import com.uni.pe.storyhub.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlogMapperTest {

    @Autowired
    private BlogMapper blogMapper;

    @Test
    void toResponse_ShouldMapCorrectly() {
        User author = new User();
        author.setNombreCompleto("Juan Perez");

        Tag tag = new Tag();
        tag.setIdTag(1);
        tag.setNombre("Java");
        Set<Tag> tags = new HashSet<>(Collections.singletonList(tag));

        Blog blog = new Blog();
        blog.setIdBlog(1);
        blog.setTitulo("Test Blog");
        blog.setVistasCount(10);
        blog.setAuthor(author);
        blog.setTags(tags);

        BlogResponse response = blogMapper.toResponse(blog);

        assertNotNull(response);
        assertEquals(blog.getTitulo(), response.getTitulo());
        assertEquals(10, response.getVistas());
        assertEquals(1, response.getTags().size());
        assertEquals("Java", response.getTags().get(0).getName());
    }

    @Test
    void toEntity_ShouldMapCorrectly() {
        BlogRequest request = new BlogRequest();
        request.setTitulo("New Blog");
        request.setContenidoBlog("Content");

        Blog entity = blogMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getTitulo(), entity.getTitulo());
        assertEquals(0, entity.getVistasCount());
        assertEquals(0, entity.getLikes());
    }

    @Test
    void mapTagsToResponseList_ShouldHandleNull() {
        List<TagResponse> result = blogMapper.mapTagsToResponseList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateEntityFromRequest_ShouldUpdateFields() {
        Blog blog = new Blog();
        blog.setTitulo("Old Title");

        BlogRequest request = new BlogRequest();
        request.setTitulo("Updated Title");

        blogMapper.updateEntityFromRequest(request, blog);

        assertEquals("Updated Title", blog.getTitulo());
    }
}

package com.uni.pe.storyhub.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.pe.storyhub.application.dto.request.BlogRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateBlogRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.BlogResponse;
import com.uni.pe.storyhub.application.dto.response.TagResponse;
import com.uni.pe.storyhub.application.port.in.BlogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BlogControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BlogService blogService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(username = "test@test.com")
        void createBlog_ShouldReturnCreated() throws Exception {
                BlogRequest request = new BlogRequest();
                request.setTitulo("Test Blog");
                request.setContenidoBlog("Contenido de prueba");

                when(blogService.createBlog(any(), anyString()))
                                .thenReturn(ApiResponse.<BlogResponse>builder().statusCode(201).build());

                mockMvc.perform(post("/api/blogs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void updateBlog_ShouldReturnOk() throws Exception {
                UpdateBlogRequest request = new UpdateBlogRequest();
                request.setTitulo("Updated title");
                request.setContenidoBlog("Updated content which should be long enough usually but service is mocked");

                when(blogService.updateBlog(anyInt(), any(), anyString()))
                                .thenReturn(ApiResponse.<BlogResponse>builder().statusCode(200).build());

                mockMvc.perform(put("/api/blogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void updateBlog_WithExtraFields_ShouldReturnBadRequest() throws Exception {
                String jsonWithExtraField = "{\"titulo\":\"New Title\", \"extra_field\":\"forbidden\"}";

                mockMvc.perform(put("/api/blogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonWithExtraField))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void deleteBlog_ShouldReturnOk() throws Exception {
                when(blogService.deleteBlog(anyInt(), anyString()))
                                .thenReturn(ApiResponse.<Void>builder().statusCode(200).build());

                mockMvc.perform(delete("/api/blogs/1"))
                                .andExpect(status().isOk());
        }

        @ParameterizedTest
        @CsvSource({
                        "127.0.0.1",
                        "unknown",
                        "''",
                        "MISSING"
        })
        @WithMockUser(username = "anonymousUser")
        void getBlogBySlug_ForwardedHeaderScenarios(String headerValue) throws Exception {
                when(blogService.getBlogBySlug(anyString(), anyString(), anyString(), anyString()))
                                .thenReturn(ApiResponse.<BlogResponse>builder().statusCode(200).build());

                var requestBuilder = get("/api/blogs/test-slug")
                                .header("User-Agent", "Mozilla");

                if (!"MISSING".equals(headerValue)) {
                        requestBuilder.header("X-Forwarded-For", headerValue);
                }

                mockMvc.perform(requestBuilder)
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getAllPublicBlogs_ShouldReturnOk() throws Exception {
                Page<BlogResponse> page = new PageImpl<>(Collections.emptyList());
                when(blogService.getAllPublicBlogs(any(), anyString()))
                                .thenReturn(ApiResponse.<Page<BlogResponse>>builder().statusCode(200).data(page)
                                                .build());

                mockMvc.perform(get("/api/blogs"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void getMyBlogs_ShouldReturnOk() throws Exception {
                Page<BlogResponse> page = new PageImpl<>(Collections.emptyList());
                when(blogService.getBlogsByUser(anyString(), any(), anyString()))
                                .thenReturn(ApiResponse.<Page<BlogResponse>>builder().statusCode(200).data(page)
                                                .build());

                mockMvc.perform(get("/api/blogs/me"))
                                .andExpect(status().isOk());
        }

        @Test
        void getAllTags_ShouldReturnOk() throws Exception {
                List<TagResponse> tags = Collections.emptyList();
                when(blogService.getAllTags())
                                .thenReturn(ApiResponse.<List<TagResponse>>builder().statusCode(200).data(tags)
                                                .build());

                mockMvc.perform(get("/api/blogs/tags"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void giveLike_ShouldReturnOk() throws Exception {
                when(blogService.giveLike(anyInt(), anyString()))
                                .thenReturn(ApiResponse.<Void>builder().statusCode(200).build());

                mockMvc.perform(post("/api/blogs/1/like"))
                                .andExpect(status().isOk());
        }
}

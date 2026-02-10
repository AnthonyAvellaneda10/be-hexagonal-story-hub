package com.uni.pe.storyhub.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.pe.storyhub.application.dto.request.CommentRequest;
import com.uni.pe.storyhub.application.dto.request.UpdateCommentRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.CommentResponse;
import com.uni.pe.storyhub.application.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CommentService commentService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(username = "test@test.com")
        void addComment_ShouldReturnCreated() throws Exception {
                CommentRequest request = new CommentRequest();
                request.setComentario("Test comment");

                when(commentService.addComment(anyInt(), any(), anyString()))
                                .thenReturn(ApiResponse.<CommentResponse>builder().statusCode(201).build());

                mockMvc.perform(post("/api/comments/blog/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        void getCommentsByBlog_ShouldReturnOk() throws Exception {
                Page<CommentResponse> page = new PageImpl<>(Collections.emptyList());
                when(commentService.getCommentsByBlog(anyInt(), any()))
                                .thenReturn(ApiResponse.<Page<CommentResponse>>builder().statusCode(200)
                                                .data(page)
                                                .build());

                mockMvc.perform(get("/api/comments/blog/1"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void deleteComment_ShouldReturnOk() throws Exception {
                when(commentService.deleteComment(anyInt(), anyString()))
                                .thenReturn(ApiResponse.<Void>builder().statusCode(200).build());

                mockMvc.perform(delete("/api/comments/1"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void updateComment_ShouldReturnOk() throws Exception {
                UpdateCommentRequest request = new UpdateCommentRequest();
                request.setComentario("Updated comment");

                when(commentService.updateComment(anyInt(), any(), anyString()))
                                .thenReturn(ApiResponse.<CommentResponse>builder().statusCode(200).build());

                mockMvc.perform(put("/api/comments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        void upvoteComment_ShouldReturnOk() throws Exception {
                when(commentService.upvoteComment(anyInt()))
                                .thenReturn(ApiResponse.<Void>builder().statusCode(200).build());

                mockMvc.perform(post("/api/comments/1/upvote"))
                                .andExpect(status().isOk());
        }
}

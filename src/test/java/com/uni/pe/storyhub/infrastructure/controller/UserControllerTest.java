package com.uni.pe.storyhub.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.pe.storyhub.application.dto.request.UpdateUserRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserProfileResponse;
import com.uni.pe.storyhub.application.dto.response.PublicUserResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(username = "test@test.com")
        void getProfile_ShouldReturnOk() throws Exception {
                UserResponse userResponse = new UserResponse();
                userResponse.setEmail("test@test.com");
                userResponse.setUsername("testuser");

                ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                                .statusCode(200)
                                .data(userResponse)
                                .build();

                when(userService.getProfile(anyString())).thenReturn(apiResponse);

                mockMvc.perform(get("/api/users/perfil"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.email").value("test@test.com"));
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void updateProfile_ShouldReturnOk() throws Exception {
                UpdateUserRequest request = UpdateUserRequest.builder()
                                .username("newusername")
                                .descripcion("New Bio")
                                .build();

                UserResponse userResponse = new UserResponse();
                userResponse.setUsername("newusername");
                userResponse.setDescripcion("New Bio");

                ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                                .statusCode(200)
                                .message("Perfil actualizado exitosamente")
                                .data(userResponse)
                                .build();

                when(userService.updateProfile(anyString(), any(UpdateUserRequest.class))).thenReturn(apiResponse);

                mockMvc.perform(put("/api/users/perfil")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Perfil actualizado exitosamente"))
                                .andExpect(jsonPath("$.data.username").value("newusername"));
        }

        @Test
        @WithMockUser(username = "test@test.com")
        void updateProfile_WithExtraFields_ShouldReturnBadRequest() throws Exception {
                String jsonWithExtraField = "{\"username\":\"newuser\", \"email\":\"wrong@field.com\"}";

                mockMvc.perform(put("/api/users/perfil")
                                .contentType("application/json")
                                .content(jsonWithExtraField))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void getPublicProfile_ShouldReturnOk() throws Exception {
                String username = "testuser";
                when(userService.getPublicProfile(eq(username), any()))
                                .thenReturn(ApiResponse.<PublicUserProfileResponse>builder()
                                                .statusCode(200)
                                                .data(PublicUserProfileResponse.builder()
                                                                .user(PublicUserResponse.builder().username(username)
                                                                                .build())
                                                                .build())
                                                .build());

                mockMvc.perform(get("/api/users/" + username))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.user.username").value(username));
        }
}

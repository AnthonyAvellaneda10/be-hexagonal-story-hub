package com.uni.pe.storyhub.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.pe.storyhub.application.dto.request.AuthRequest;
import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.request.ResetPasswordRequest;
import com.uni.pe.storyhub.application.dto.request.UpdatePasswordRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.AuthResponse;
import com.uni.pe.storyhub.application.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void register_ShouldReturnCreated() throws Exception {
                RegisterRequest request = new RegisterRequest();
                request.setEmail("test@test.com");
                request.setNombreCompleto("Juan Perez");
                request.setUsername("juanp");
                request.setPassword("Password123!");

                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                                .statusCode(201)
                                .message("User registered")
                                .build();

                when(authService.register(any(RegisterRequest.class))).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("User registered"));
        }

        @Test
        void login_ShouldSetCookie() throws Exception {
                AuthRequest request = new AuthRequest();
                request.setEmail("test@test.com");
                request.setPassword("password");

                AuthResponse authResponse = new AuthResponse();
                authResponse.setAccessToken("access");
                authResponse.setRefreshToken("refresh");

                ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .data(authResponse)
                                .build();

                when(authService.login(any(AuthRequest.class))).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("refreshToken"))
                                .andExpect(cookie().value("refreshToken", "refresh"))
                                .andExpect(cookie().httpOnly("refreshToken", true));
        }

        @Test
        void refresh_ShouldUpdateCookie() throws Exception {
                AuthResponse authResponse = new AuthResponse();
                authResponse.setAccessToken("new-access");
                authResponse.setRefreshToken("new-refresh");

                ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .data(authResponse)
                                .build();

                when(authService.refreshToken(anyString())).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(new Cookie("refreshToken", "old-refresh")))
                                .andExpect(status().isOk())
                                .andExpect(cookie().value("refreshToken", "new-refresh"));
        }

        @Test
        void refresh_NoCookie_ShouldThrowException() throws Exception {
                mockMvc.perform(post("/api/auth/refresh"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void logout_ShouldClearCookie() throws Exception {
                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.logout(any())).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/logout")
                                .cookie(new Cookie("refreshToken", "some-token")))
                                .andExpect(status().isOk())
                                .andExpect(cookie().maxAge("refreshToken", 0));
        }

        @Test
        void logout_NoCookie_ShouldStillReturnOk() throws Exception {
                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.logout(null)).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/logout"))
                                .andExpect(status().isOk());
        }

        @Test
        void verify_ShouldReturnOk() throws Exception {
                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.verifyEmail(anyString(), anyString())).thenReturn(apiResponse);

                mockMvc.perform(get("/api/auth/verificar")
                                .param("email", "test@test.com")
                                .param("code", "12345"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "user@test.com")
        void updatePassword_ShouldReturnOk() throws Exception {
                UpdatePasswordRequest request = new UpdatePasswordRequest();
                request.setCurrentPassword("old-password");
                request.setNewPassword("NewPassword123!");

                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.updatePassword(any(), anyString())).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/update-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        void forgotPassword_ShouldReturnOk() throws Exception {
                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.forgotPassword(anyString())).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/forgot-password")
                                .param("email", "test@test.com"))
                                .andExpect(status().isOk());
        }

        @Test
        void login_NullData_ShouldNotSetCookie() throws Exception {
                AuthRequest request = new AuthRequest();
                request.setEmail("test@test.com");
                request.setPassword("password");

                ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .data(null)
                                .build();

                when(authService.login(any(AuthRequest.class))).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().doesNotExist("refreshToken"));
        }

        @Test
        void login_NullRefreshToken_ShouldNotSetCookie() throws Exception {
                AuthRequest request = new AuthRequest();
                request.setEmail("test@test.com");
                request.setPassword("password");

                AuthResponse authResponse = new AuthResponse();
                authResponse.setAccessToken("access");
                authResponse.setRefreshToken(null);

                ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                                .statusCode(200)
                                .data(authResponse)
                                .build();

                when(authService.login(any(AuthRequest.class))).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().doesNotExist("refreshToken"));
        }

        @Test
        void resetPassword_ShouldReturnOk() throws Exception {
                ResetPasswordRequest request = new ResetPasswordRequest();
                request.setEmail("test@test.com");
                request.setCode("123456");
                request.setNewPassword("NewPassword123!");

                ApiResponse<Void> apiResponse = ApiResponse.<Void>builder().statusCode(200).build();
                when(authService.resetPassword(any())).thenReturn(apiResponse);

                mockMvc.perform(post("/api/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }
}

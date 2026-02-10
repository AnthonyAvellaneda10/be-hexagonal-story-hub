package com.uni.pe.storyhub.application.service;

import com.uni.pe.storyhub.application.dto.request.AuthRequest;
import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.request.UpdatePasswordRequest;
import com.uni.pe.storyhub.application.dto.request.ResetPasswordRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.AuthResponse;

public interface AuthService {
    ApiResponse<Void> register(RegisterRequest registerRequest);

    ApiResponse<AuthResponse> login(AuthRequest authRequest);

    ApiResponse<Void> verifyEmail(String email, String code);

    ApiResponse<AuthResponse> refreshToken(String refreshToken);

    ApiResponse<Void> logout(String refreshToken);

    ApiResponse<Void> updatePassword(UpdatePasswordRequest request, String userEmail);

    ApiResponse<Void> forgotPassword(String email);

    ApiResponse<Void> resetPassword(ResetPasswordRequest request);
}

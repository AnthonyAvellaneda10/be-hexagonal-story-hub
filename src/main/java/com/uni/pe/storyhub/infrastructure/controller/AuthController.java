package com.uni.pe.storyhub.infrastructure.controller;

import com.uni.pe.storyhub.application.dto.request.AuthRequest;
import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.AuthResponse;
import com.uni.pe.storyhub.application.service.AuthService;
import com.uni.pe.storyhub.application.dto.request.UpdatePasswordRequest;
import com.uni.pe.storyhub.application.dto.request.ResetPasswordRequest;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refreshExpiration}")
    private int refreshExpiration;

    @Value("${app.security.secure-cookies:false}")
    private boolean secureCookies;

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<Void> response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest,
            HttpServletResponse response) {
        ApiResponse<AuthResponse> apiResponse = authService.login(authRequest);
        handleRefreshToken(response, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            throw new BusinessException("No se encontró el refresh token en las cookies", 0, 401);
        }

        ApiResponse<AuthResponse> apiResponse = authService.refreshToken(refreshToken);
        handleRefreshToken(response, apiResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        ApiResponse<Void> apiResponse = authService.logout(refreshToken);
        clearCookie(response);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ApiResponse<Void> response = authService.updatePassword(request, email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Email(message = "Formato de correo inválido") @NotBlank(message = "El correo es obligatorio") @RequestParam String email) {
        ApiResponse<Void> response = authService.forgotPassword(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse<Void> response = authService.resetPassword(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void handleRefreshToken(HttpServletResponse response, ApiResponse<AuthResponse> apiResponse) {
        if (apiResponse.getData() != null && apiResponse.getData().getRefreshToken() != null) {
            setCookie(response, apiResponse.getData().getRefreshToken());
            apiResponse.getData().setRefreshToken(null);
        }
    }

    private void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookies);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private void setCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookies);
        cookie.setPath("/");
        cookie.setMaxAge(refreshExpiration / 1000); // ms a segundos
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    @GetMapping("/verificar")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Email(message = "Formato de correo inválido") @NotBlank(message = "El correo es obligatorio") @RequestParam String email,
            @RequestParam String code) {
        ApiResponse<Void> response = authService.verifyEmail(email, code);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

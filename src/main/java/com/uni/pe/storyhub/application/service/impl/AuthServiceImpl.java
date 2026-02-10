package com.uni.pe.storyhub.application.service.impl;

import com.uni.pe.storyhub.application.dto.request.UpdatePasswordRequest;
import com.uni.pe.storyhub.application.dto.request.ResetPasswordRequest;
import com.uni.pe.storyhub.application.dto.request.AuthRequest;
import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.AuthResponse;
import com.uni.pe.storyhub.application.dto.response.UserResponse;
import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.service.AuthService;
import com.uni.pe.storyhub.application.service.EmailService;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.entity.UserSession;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.domain.repository.UserSessionRepository;
import com.uni.pe.storyhub.infrastructure.security.JwtProvider;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import lombok.RequiredArgsConstructor;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.exception.LoginLockoutException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final ToastIdGenerator toastIdGenerator;

    private static final String USER_NOT_FOUND = "Usuario no encontrado";
    private static final String SUCCESS_TYPE = ApiResponse.TYPE_SUCCESS;
    private static final String IMAGE_USER_PROFILE = "assets/images/user-profile.png";

    @Override
    @Transactional
    public ApiResponse<Void> register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("El email ya está registrado", 0, 400);
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException("El nombre de usuario ya está en uso", 0, 400);
        }

        User user = authMapper.toEntity(registerRequest);

        // Generate username if not provided
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            String emailPrefix = user.getEmail().split("@")[0];
            user.setUsername(emailPrefix);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivo(true);
        user.setImagenPerfil(IMAGE_USER_PROFILE);
        user.setEmailVerificado(false);
        user.setCodigoVerificacion(UUID.randomUUID().toString().substring(0, 8));

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getCodigoVerificacion());

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Registro exitoso. Revisa tu correo para verificar tu cuenta.")
                .type(SUCCESS_TYPE)
                .statusCode(201)
                .build();
    }

    @Override
    public ApiResponse<AuthResponse> login(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new LoginLockoutException("Credenciales incorrectas", 0, 401));

        if (!user.isEmailVerificado()) {
            throw new LoginLockoutException("Debes verificar tu email antes de iniciar sesión", 0, 403);
        }

        // Check if user is locked out
        if (user.getFechaBloqueo() != null) {
            if (user.getFechaBloqueo().isAfter(LocalDateTime.now())) {
                throw new LoginLockoutException("Acceso bloqueado. Intenta de nuevo en 1 minuto.", 2, 403);
            } else {
                // Reset if lockout period has passed
                user.setIntentosFallidos(0);
                user.setFechaBloqueo(null);
                userRepository.save(user);
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            // Success: Reset attempts
            user.setIntentosFallidos(0);
            user.setFechaBloqueo(null);
            userRepository.save(user);

        } catch (Exception e) {
            // Failure: Handle attempts
            int currentAttempts = (user.getIntentosFallidos() == null ? 0 : user.getIntentosFallidos()) + 1;
            user.setIntentosFallidos(currentAttempts);

            String message;
            int idToast = 0;
            if (currentAttempts == 1) {
                message = "Credenciales incorrectas. Te quedan 2 intentos.";
            } else if (currentAttempts == 2) {
                message = "Credenciales incorrectas. Te queda 1 intento.";
            } else {
                user.setFechaBloqueo(LocalDateTime.now().plusMinutes(1));
                message = "Acceso bloqueado. Intenta de nuevo en 1 minuto.";
                idToast = 2;
            }

            userRepository.save(user);
            throw new LoginLockoutException(message, idToast, 401);
        }

        String accessToken = jwtProvider.generateTokenFromEmail(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Save session
        userSessionRepository.deleteByUser_IdUsuario(user.getIdUsuario());
        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiraAt(LocalDateTime.now().plusDays(7))
                .build();
        userSessionRepository.save(session);

        UserResponse userResponse = authMapper.toUserResponse(user);

        AuthResponse authResponse = AuthResponse.builder()
                .userResponse(userResponse)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ApiResponse.<AuthResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Login exitoso")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(authResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        if (user.isEmailVerificado()) {
            throw new BusinessException("El email ya ha sido verificado", 0, 400);
        }

        if (!user.getCodigoVerificacion().equals(code)) {
            throw new BusinessException("Código de verificación incorrecto", 0, 400);
        }

        user.setEmailVerificado(true);
        user.setCodigoVerificacion(null);
        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Email verificado exitosamente. Ya puedes iniciar sesión.")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException("Refresh Token inválido o expirado", 0, 401);
        }

        String email = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Sesión no encontrada", 0, 404));

        if (session.getExpiraAt().isBefore(LocalDateTime.now())) {
            userSessionRepository.delete(session);
            throw new BusinessException("La sesión ha expirado", 0, 401);
        }

        // Generate new tokens
        String newAccessToken = jwtProvider.generateTokenFromEmail(user.getEmail());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Update session
        session.setRefreshToken(newRefreshToken);
        session.setExpiraAt(LocalDateTime.now().plusDays(7));
        userSessionRepository.save(session);

        AuthResponse authResponse = AuthResponse.builder()
                .userResponse(authMapper.toUserResponse(user))
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ApiResponse.<AuthResponse>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Token refrescado")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .data(authResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(String refreshToken) {
        if (refreshToken != null) {
            userSessionRepository.findByRefreshToken(refreshToken)
                    .ifPresent(userSessionRepository::delete);
        }

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Sesión cerrada exitosamente")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> updatePassword(UpdatePasswordRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta", 0, 400);
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException("La nueva contraseña debe ser diferente a la actual", 0, 400);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Contraseña actualizada exitosamente")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("No existe una cuenta asociada a este email", 0, 404));

        String resetCode = UUID.randomUUID().toString().substring(0, 8);
        user.setCodigoVerificacion(resetCode);
        userRepository.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), resetCode);

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Código de verificación reenviado.")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND, 0, 404));

        if (!request.getCode().equals(user.getCodigoVerificacion())) {
            throw new BusinessException("Código de recuperación inválido", 0, 400);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setCodigoVerificacion(null); // Clear the code after use
        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .idToast(toastIdGenerator.nextId())
                .message("Se ha enviado un correo para restablecer tu contraseña")
                .type(SUCCESS_TYPE)
                .statusCode(200)
                .build();
    }
}

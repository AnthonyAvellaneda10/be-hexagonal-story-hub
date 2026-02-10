package com.uni.pe.storyhub.application.usecase;

import com.uni.pe.storyhub.application.dto.request.AuthRequest;
import com.uni.pe.storyhub.application.dto.request.RegisterRequest;
import com.uni.pe.storyhub.application.dto.request.ResetPasswordRequest;
import com.uni.pe.storyhub.application.dto.request.UpdatePasswordRequest;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.application.dto.response.AuthResponse;
import com.uni.pe.storyhub.application.mapper.AuthMapper;
import com.uni.pe.storyhub.application.port.in.EmailService;
import com.uni.pe.storyhub.domain.entity.User;
import com.uni.pe.storyhub.domain.entity.UserSession;
import com.uni.pe.storyhub.domain.repository.UserRepository;
import com.uni.pe.storyhub.domain.repository.UserSessionRepository;
import com.uni.pe.storyhub.infrastructure.exception.BusinessException;
import com.uni.pe.storyhub.infrastructure.exception.LoginLockoutException;
import com.uni.pe.storyhub.infrastructure.config.JwtProvider;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSessionRepository userSessionRepository;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private EmailService emailService;
    @Mock
    private ToastIdGenerator toastIdGenerator;

    @InjectMocks
    private AuthUseCase authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        authRequest = new AuthRequest();
        authRequest.setEmail("test@test.com");
        authRequest.setPassword("password");

        user = new User();
        user.setIdUsuario(1);
        user.setEmail("test@test.com");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmailVerificado(true);
        user.setActivo(true);
        user.setCodigoVerificacion("code123");
    }

    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(authMapper.toEntity(any())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));
        assertEquals("El email ya está registrado", exception.getMessage());
    }

    @Test
    void register_UsernameAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));
        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtProvider.generateTokenFromEmail(anyString())).thenReturn("accessToken");
        when(jwtProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<AuthResponse> response = authService.login(authRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Login exitoso", response.getMessage());
        assertNotNull(response.getData());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginLockoutException exception = assertThrows(LoginLockoutException.class,
                () -> authService.login(authRequest));
        assertEquals("Credenciales incorrectas", exception.getMessage());
    }

    @Test
    void register_NullUsername_GeneratesFromEmail() {
        registerRequest.setUsername(null);
        user.setUsername(null);
        user.setEmail("test@test.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(null)).thenReturn(false);
        when(authMapper.toEntity(any())).thenReturn(user);
        when(toastIdGenerator.nextId()).thenReturn(1);

        authService.register(registerRequest);

        assertEquals("test", user.getUsername());
    }

    @Test
    void register_BlankUsername_GeneratesFromEmail() {
        registerRequest.setUsername(" ");
        user.setUsername(" ");
        user.setEmail("test@test.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(authMapper.toEntity(any())).thenReturn(user);
        when(toastIdGenerator.nextId()).thenReturn(1);

        authService.register(registerRequest);

        assertEquals("test", user.getUsername());
    }

    @Test
    void login_EmailNotVerified() {
        user.setEmailVerificado(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        LoginLockoutException exception = assertThrows(LoginLockoutException.class,
                () -> authService.login(authRequest));
        assertEquals("Debes verificar tu email antes de iniciar sesión", exception.getMessage());
    }

    @Test
    void login_NullAttempts_InitializesTo1() {
        user.setIntentosFallidos(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Auth failed")).when(authenticationManager).authenticate(any());

        assertThrows(LoginLockoutException.class, () -> authService.login(authRequest));
        assertEquals(1, user.getIntentosFallidos());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        when(jwtProvider.validateToken(anyString())).thenReturn(false);
        assertThrows(BusinessException.class, () -> authService.refreshToken("invalid"));
    }

    @Test
    void refreshToken_NullToken_ThrowsException() {
        assertThrows(BusinessException.class, () -> authService.refreshToken(null));
    }

    @Test
    void logout_NonNull_Success() {
        when(userSessionRepository.findByRefreshToken(anyString())).thenReturn(Optional.empty());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.logout("some-token");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void logout_NullToken_Success() {
        when(toastIdGenerator.nextId()).thenReturn(1);
        ApiResponse<Void> response = authService.logout(null);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void updatePassword_UserNotFound() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> authService.updatePassword(request, "none@test.com"));
    }

    @Test
    void forgotPassword_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> authService.forgotPassword("none@test.com"));
    }

    @Test
    void resetPassword_UserNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("none@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> authService.resetPassword(request));
    }

    @ParameterizedTest
    @CsvSource({
            "2, Acceso bloqueado",
            "1, 1 intento",
            "0, 2 intentos"
    })
    void login_FailedAttempts_Scenarios(int initialAttempts, String expectedMessagePart) {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Auth failed")).when(authenticationManager).authenticate(any());
        user.setIntentosFallidos(initialAttempts);

        LoginLockoutException exception = assertThrows(LoginLockoutException.class,
                () -> authService.login(authRequest));
        assertTrue(exception.getMessage().contains(expectedMessagePart));
    }

    @Test
    void login_AlreadyLocked_ThrowsException() {
        user.setFechaBloqueo(LocalDateTime.now().plusMinutes(5));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        LoginLockoutException exception = assertThrows(LoginLockoutException.class,
                () -> authService.login(authRequest));
        assertTrue(exception.getMessage().contains("Acceso bloqueado"));
    }

    @Test
    void login_WithLockoutExpired_ResetsAttempts() {
        user.setFechaBloqueo(LocalDateTime.now().minusMinutes(5));
        user.setIntentosFallidos(3);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtProvider.generateTokenFromEmail(anyString())).thenReturn("accessToken");
        when(jwtProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<AuthResponse> response = authService.login(authRequest);

        assertEquals(0, user.getIntentosFallidos());
        assertNull(user.getFechaBloqueo());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void verifyEmail_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        user.setEmailVerificado(false);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.verifyEmail("test@test.com", "code123");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(user.isEmailVerificado());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void verifyEmail_AlreadyVerified_ThrowsException() {
        user.setEmailVerificado(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.verifyEmail("test@test.com", "code123"));
        assertEquals("El email ya ha sido verificado", exception.getMessage());
    }

    @Test
    void verifyEmail_WrongCode() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        user.setEmailVerificado(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.verifyEmail("test@test.com", "wrong"));
        assertEquals("Código de verificación incorrecto", exception.getMessage());
    }

    @Test
    void verifyEmail_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.verifyEmail("test@test.com", "code123"));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void refreshToken_Success() {
        UserSession session = UserSession.builder()
                .refreshToken("oldToken")
                .expiraAt(LocalDateTime.now().plusDays(1))
                .build();

        when(jwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(anyString())).thenReturn("test@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userSessionRepository.findByRefreshToken(anyString())).thenReturn(Optional.of(session));
        when(jwtProvider.generateTokenFromEmail(anyString())).thenReturn("newAccess");
        when(jwtProvider.generateRefreshToken(anyString())).thenReturn("newRefresh");
        when(authMapper.toUserResponse(any()))
                .thenReturn(com.uni.pe.storyhub.application.dto.response.UserResponse.builder().build());
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<AuthResponse> response = authService.refreshToken("oldToken");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("newAccess", response.getData().getAccessToken());
        assertNotNull(response.getData().getUserResponse());
    }

    @Test
    void refreshToken_InvalidToken() {
        when(jwtProvider.validateToken(anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.refreshToken("invalid"));
        assertEquals("Refresh Token inválido o expirado", exception.getMessage());
    }

    @Test
    void refreshToken_SessionNotFound() {
        when(jwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(anyString())).thenReturn("test@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userSessionRepository.findByRefreshToken(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.refreshToken("token"));
        assertEquals("Sesión no encontrada", exception.getMessage());
    }

    @Test
    void refreshToken_SessionExpired() {
        UserSession session = UserSession.builder()
                .expiraAt(LocalDateTime.now().minusDays(1))
                .build();
        when(jwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(anyString())).thenReturn("test@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userSessionRepository.findByRefreshToken(anyString())).thenReturn(Optional.of(session));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.refreshToken("token"));
        assertEquals("La sesión ha expirado", exception.getMessage());
    }

    @Test
    void refreshToken_UserNotFound_ThrowsException() {
        when(jwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(anyString())).thenReturn("none@test.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.refreshToken("token"));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void logout_Success() {
        when(userSessionRepository.findByRefreshToken(anyString())).thenReturn(Optional.of(new UserSession()));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.logout("token");

        assertNotNull(response);
    }

    @Test
    void updatePassword_Success() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("old");
        request.setNewPassword("new");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.updatePassword(request, "test@test.com");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updatePassword_WrongCurrentPassword_ThrowsException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("new");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.updatePassword(request, "test@test.com"));
        assertEquals("La contraseña actual es incorrecta", exception.getMessage());
    }

    @Test
    void updatePassword_NewPasswordSameAsCurrent_ThrowsException() {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("old");
        request.setNewPassword("old");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPassword())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.updatePassword(request, "test@test.com"));
        assertEquals("La nueva contraseña debe ser diferente a la actual", exception.getMessage());
    }

    @Test
    void forgotPassword_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.forgotPassword("test@test.com");

        assertNotNull(response);
        verify(emailService, times(1)).sendResetPasswordEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_Success() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@test.com");
        request.setCode("code123");
        request.setNewPassword("newPass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncoded");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ApiResponse<Void> response = authService.resetPassword(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetPassword_WrongCode_ThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@test.com");
        request.setCode("wrong");
        request.setNewPassword("newPass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.resetPassword(request));
        assertEquals("Código de recuperación inválido", exception.getMessage());
    }
}

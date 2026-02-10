package com.uni.pe.storyhub.infrastructure.exception;

import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ToastIdGenerator toastIdGenerator;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleBusinessException_WithIdToast_ReturnsCorrectResponse() {
        BusinessException ex = new BusinessException("Business error", 50, 400);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(50, responseEntity.getBody().getIdToast());
    }

    @Test
    void handleLoginLockoutException_ReturnsCorrectResponse() {
        LoginLockoutException ex = new LoginLockoutException("Lockout error", 2, 403);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleLoginLockoutException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(2, responseEntity.getBody().getIdToast());
    }

    @Test
    void handleBusinessException_DefaultStatusCode_ReturnsBadRequest() {
        BusinessException ex = new BusinessException("Business error", 0, 400);
        when(toastIdGenerator.nextId()).thenReturn(101);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleLoginLockoutException_DefaultStatusCode_ReturnsBadRequest() {
        LoginLockoutException ex = new LoginLockoutException("Lockout error", 0, 400);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleLoginLockoutException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void handleRuntimeException_ReturnsInternalError() {
        RuntimeException ex = new RuntimeException("Generic error");
        when(toastIdGenerator.nextId()).thenReturn(500);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleRuntimeException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Generic error", responseEntity.getBody().getMessage());
    }

    @Test
    void handleGeneralException_ReturnsFriendlyMessage() {
        Exception ex = new Exception("Critical error");
        when(toastIdGenerator.nextId()).thenReturn(500);

        ResponseEntity<ApiResponse<Void>> responseEntity = globalExceptionHandler.handleGeneralException(ex);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getMessage().contains("error inesperado"));
    }

    @Test
    void handleMethodArgumentNotValidException_ReturnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Map<String, String>>> response = globalExceptionHandler
                .handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error de validación"));
        assertEquals("message", response.getBody().getData().get("field"));
    }

    @Test
    void handleConstraintViolationException_ReturnsBadRequest() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("violation message");
        Set<? extends ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException("msg", violations);

        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("violation message"));
    }

    @Test
    void handleHttpMessageNotReadableException_ReturnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("JSON"));
    }

    @Test
    void handleMissingServletRequestParameterException_ReturnsBadRequest() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "type");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler
                .handleMissingServletRequestParameterException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("param"));
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ReturnsBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("param");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler
                .handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("param"));
    }

    @Test
    void handleNoResourceFoundException_ReturnsNotFound() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getResourcePath()).thenReturn("/unknown");
        when(toastIdGenerator.nextId()).thenReturn(1);

        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleNoResourceFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("/unknown"));
    }
}

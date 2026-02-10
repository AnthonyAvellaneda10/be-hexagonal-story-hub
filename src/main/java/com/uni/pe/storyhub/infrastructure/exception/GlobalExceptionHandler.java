package com.uni.pe.storyhub.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import com.uni.pe.storyhub.application.dto.response.ApiResponse;
import com.uni.pe.storyhub.infrastructure.util.ToastIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

        private final ToastIdGenerator toastIdGenerator;

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                String message = "Error de validación: " + errors.values().stream().collect(Collectors.joining(", "));

                ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message(message)
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .data(errors)
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
                        jakarta.validation.ConstraintViolationException ex) {

                String message = ex.getConstraintViolations().stream()
                                .map(jakarta.validation.ConstraintViolation::getMessage)
                                .collect(Collectors.joining(", "));

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Error de parámetro: " + message)
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("El cuerpo de la petición (JSON) es obligatorio o está mal formado. " +
                                                "Asegúrate de enviar un JSON válido en el cuerpo (Body) de la petición.")
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
                        MissingServletRequestParameterException ex) {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Falta el parámetro obligatorio: " + ex.getParameterName())
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("El parámetro '" + ex.getName() + "' tiene un valor inválido.")
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(LoginLockoutException.class)
        public ResponseEntity<ApiResponse<Void>> handleLoginLockoutException(LoginLockoutException ex) {
                int idToast = ex.getIdToast() != 0 ? ex.getIdToast() : toastIdGenerator.nextId();
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(idToast)
                                .message(ex.getMessage())
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(ex.getStatusCode())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
                int idToast = ex.getIdToast() != 0 ? ex.getIdToast() : toastIdGenerator.nextId();
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(idToast)
                                .message(ex.getMessage())
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(ex.getStatusCode())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message(ex.getMessage())
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
                        org.springframework.web.servlet.resource.NoResourceFoundException ex) {
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("La ruta solicitada no existe: " + ex.getResourcePath())
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.NOT_FOUND.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
                log.error("Ocurrió un error inesperado", ex);
                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .idToast(toastIdGenerator.nextId())
                                .message("Ocurrió un error inesperado. Por favor, contacte al soporte.")
                                .type(ApiResponse.TYPE_ERROR)
                                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .build();

                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}

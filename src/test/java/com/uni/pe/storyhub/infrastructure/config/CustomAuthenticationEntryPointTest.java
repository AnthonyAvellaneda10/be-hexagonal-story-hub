package com.uni.pe.storyhub.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private CustomAuthenticationEntryPoint entryPoint;

    @Test
    void commence_ShouldReturn401Json() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getAttribute("jwt_error")).thenReturn("Token expired");

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        String result = stringWriter.toString();
        assertTrue(result.contains("Token expired"));
        assertTrue(result.contains("\"statusCode\":401"));
    }

    @Test
    void commence_NoJwtError_ShouldReturnGenericMessage() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getAttribute("jwt_error")).thenReturn(null);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String result = stringWriter.toString();
        assertTrue(result.contains("No autorizado. Debe proporcionar un token válido"));
    }
}

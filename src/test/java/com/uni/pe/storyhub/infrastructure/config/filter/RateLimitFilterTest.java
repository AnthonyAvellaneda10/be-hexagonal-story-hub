package com.uni.pe.storyhub.infrastructure.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @Test
    void doFilterInternal_AllowedRequest() throws Exception {
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getRequestURI()).thenReturn("/api/blogs");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, atLeastOnce()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithXForwardedFor() throws Exception {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18");
        when(request.getRequestURI()).thenReturn("/api/test");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @ParameterizedTest
    @CsvSource({
            "1.1.1.1, /auth/registro, 3",
            "2.2.2.2, /auth/login, 5",
            "3.3.3.3, /auth/refresh, 20",
            "4.4.4.4, /general/endpoint, 100"
    })
    void doFilterInternal_RateLimited(String ip, String path, int capacity) throws Exception {
        when(request.getRemoteAddr()).thenReturn(ip);
        when(request.getRequestURI()).thenReturn(path);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Consume all tokens
        for (int i = 0; i < capacity; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }

        // Next request should be limited
        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(response, atLeastOnce()).setStatus(429);
        assertTrue(
                stringWriter.toString().contains("429") || stringWriter.toString().contains("Demasiadas peticiones"));
    }
}

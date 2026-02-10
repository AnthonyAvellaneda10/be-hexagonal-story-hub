package com.uni.pe.storyhub.infrastructure.config.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String AUTH_LOGIN = "/auth/login";
    private static final String AUTH_REGISTRO = "/auth/registro";
    private static final String AUTH_REFRESH = "/auth/refresh";
    private final Map<String, Map<String, Bucket>> bucketsByIpAndPath = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getClientIP(request);
        String path = request.getRequestURI();

        Bucket bucket = getBucket(ip, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"Demasiadas peticiones. Por favor, intenta de nuevo más tarde.\", \"statusCode\": 429}");
        }
    }

    private Bucket getBucket(String ip, String path) {
        return bucketsByIpAndPath
                .computeIfAbsent(ip, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(normalizePath(path), this::createNewBucket);
    }

    private Bucket createNewBucket(String path) {
        Bandwidth limit;
        if (path.contains(AUTH_REGISTRO)) {
            // Estricto: 3 peticiones por minuto
            limit = Bandwidth.builder()
                    .capacity(3)
                    .refillIntervally(3, Duration.ofMinutes(1))
                    .build();
        } else if (path.contains(AUTH_LOGIN)) {
            // Estricto: 5 peticiones por minuto
            limit = Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(5, Duration.ofMinutes(1))
                    .build();
        } else if (path.contains(AUTH_REFRESH)) {
            // Moderado para evitar el "F5 spam": 20 peticiones por minuto
            limit = Bandwidth.builder()
                    .capacity(20)
                    .refillIntervally(20, Duration.ofMinutes(1))
                    .build();
        } else {
            // General: 100 peticiones por minuto
            limit = Bandwidth.builder()
                    .capacity(100)
                    .refillIntervally(100, Duration.ofMinutes(1))
                    .build();
        }
        return Bucket.builder().addLimit(limit).build();
    }

    private String normalizePath(String path) {
        if (path.contains(AUTH_REGISTRO))
            return AUTH_REGISTRO;
        if (path.contains(AUTH_LOGIN))
            return AUTH_LOGIN;
        if (path.contains(AUTH_REFRESH))
            return AUTH_REFRESH;
        return "general";
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

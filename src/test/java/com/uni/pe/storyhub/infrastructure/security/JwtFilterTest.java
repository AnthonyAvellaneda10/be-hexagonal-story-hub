package com.uni.pe.storyhub.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_SetsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtProvider.validateToken("validToken")).thenReturn(true);
        when(jwtProvider.getUsernameFromToken("validToken")).thenReturn("test@test.com");
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithTokenPresentButInvalid_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtProvider.validateToken("invalidToken")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidBearer_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("NotBearer token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_SetsRequestAttribute() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired");
        when(jwtProvider.validateToken("expired"))
                .thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "expired"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), contains("expirado"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithSignatureException_SetsRequestAttribute() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer badsig");
        when(jwtProvider.validateToken("badsig"))
                .thenThrow(new io.jsonwebtoken.security.SignatureException("invalid signature"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), contains("firma"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedException_SetsRequestAttribute() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed");
        when(jwtProvider.validateToken("malformed"))
                .thenThrow(new io.jsonwebtoken.MalformedJwtException("malformed"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), contains("formato inválido"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithGeneralException_SetsRequestAttribute() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer error");
        when(jwtProvider.validateToken("error"))
                .thenThrow(new RuntimeException("unexpected"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("jwt_error"), contains("Error al procesar"));
        verify(filterChain).doFilter(request, response);
    }
}

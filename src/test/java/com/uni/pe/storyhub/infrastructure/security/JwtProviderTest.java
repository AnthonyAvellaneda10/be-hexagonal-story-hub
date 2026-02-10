package com.uni.pe.storyhub.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    private final String secret = "testSecretWithAtLeast256BitsForHMACSHA256Algorithm";
    private final int expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtProvider, "jwtExpiration", expiration);
        ReflectionTestUtils.setField(jwtProvider, "refreshExpiration", expiration * 24);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Test
    void generateTokenFromEmail_AndRetrieveEmail_Success() {
        String email = "test@example.com";
        String token = jwtProvider.generateTokenFromEmail(email);

        assertNotNull(token);
        String extractedEmail = jwtProvider.getUsernameFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void generateToken_Success() {
        Authentication auth = mock(Authentication.class);
        User principal = new User("test@test.com", "pass", Collections.emptyList());
        when(auth.getPrincipal()).thenReturn(principal);

        String token = jwtProvider.generateToken(auth);

        assertNotNull(token);
        assertEquals("test@test.com", jwtProvider.getUsernameFromToken(token));
    }

    @Test
    void validateToken_Success() {
        String token = jwtProvider.generateRefreshToken("user123");
        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_Failure_Malformed() {
        assertThrows(Exception.class, () -> jwtProvider.validateToken("invalidToken"));
    }

    @Test
    void validateToken_Failure_Expired() {
        String expiredToken = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> jwtProvider.validateToken(expiredToken));
    }

    @Test
    void validateToken_Failure_Signature() {
        String wrongSecret = "anotherSecretWithAtLeast256BitsForHMACSHA256Algorithm";
        Key wrongKey = Keys.hmacShaKeyFor(wrongSecret.getBytes());
        String badSigToken = Jwts.builder()
                .setSubject("user")
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(io.jsonwebtoken.security.SignatureException.class, () -> jwtProvider.validateToken(badSigToken));
    }

    @Test
    void generateRefreshToken_Success() {
        String token = jwtProvider.generateRefreshToken("user123");
        assertNotNull(token);
        assertEquals("user123", jwtProvider.getUsernameFromToken(token));
    }
}

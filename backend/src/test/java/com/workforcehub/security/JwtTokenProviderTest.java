package com.workforcehub.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "d2f8c3b4e5a6f7890123456789abcdef0123456789abcdef0123456789abcdef";
        tokenProvider = new JwtTokenProvider(secret, 900000, 604800000);
    }

    @Test
    @DisplayName("Should generate and validate access token")
    void generateAndValidateToken() {
        String token = tokenProvider.generateAccessToken("testuser");

        assertThat(token).isNotEmpty();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        assertThat(tokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("Should reject empty token")
    void shouldRejectEmptyToken() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }
}

package com.jwalit.inventory_system.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        org.springframework.test.util.ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        org.springframework.test.util.ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_createsToken() {
        String token = jwtService.generateToken("user@example.com");
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateToken("user@example.com");
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("user@example.com");
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtService.generateToken("user@example.com");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("invalid.token.string")).isFalse();
    }
    
    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Since we cannot easily inject time, let's create a JwtService with 0 ms expiration using reflection, 
        // or just wait for it to expire if we set it to 1ms.
        org.springframework.test.util.ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        String token = jwtService.generateToken("user@example.com");
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}

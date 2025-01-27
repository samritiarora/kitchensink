package com.mongo.challenge.kitchensink.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        String secret = "5KYhNagozPTwj6dYvOr1Z1rWYTwjQBQA3Nk3ky6dOAoc6s3tB4AZsCvHmVNvR4FRboMYLu5BWLPPiuzlyQy5Ph7jiXszxK4yJ4JDQXJGBGI3VLAv4/+oKrmV3z2iM8lwdsQmg4DXla+L56ggN/n1t7dbujM/0wol6G1/8h3orOBV%";
        long expiration = 3600000L;
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        jwtUtil = new JwtUtil(secret, expiration);
    }

    @Test
    public void testGenerateToken() {
        String token = jwtUtil.generateToken("user", "ROLE_USER");
        assertTrue(token != null && !token.isEmpty());
    }

    @Test
    public void testExtractUsername() {
        String token = jwtUtil.generateToken("user", "ROLE_USER");
        String username = jwtUtil.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    public void testExtractRole() {
        String token = jwtUtil.generateToken("user", "ROLE_USER");
        String role = jwtUtil.extractRole(token);
        assertEquals("ROLE_USER", role);
    }

    @Test
    public void testIsTokenValid() {
        String token = jwtUtil.generateToken("user", "ROLE_USER");
        assertTrue(jwtUtil.isTokenValid(token, "user"));
    }

    @Test
    public void testIsTokenExpired() {
        String token = jwtUtil.generateToken("user", "ROLE_USER");
        assertTrue(!jwtUtil.isTokenExpired(token));
    }
}
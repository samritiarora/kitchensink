package com.mongo.challenge.kitchensink.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@TestConfiguration
public class MockSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // Mock an admin user authentication
            Authentication auth = new TestingAuthenticationToken(
                    "admin",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return auth;
        };
    }
}


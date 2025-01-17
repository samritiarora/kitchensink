package com.mongo.challenge.kitchensink.service.impl;

import com.mongo.challenge.kitchensink.dto.AuthRequest;
import com.mongo.challenge.kitchensink.dto.AuthResponse;
import com.mongo.challenge.kitchensink.dto.SignUpRequest;
import com.mongo.challenge.kitchensink.entity.MemberEntity;
import com.mongo.challenge.kitchensink.repo.MemberRepository;
import com.mongo.challenge.kitchensink.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                       MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        String token = jwtUtil.generateToken(authenticate.getName(), authenticate.getAuthorities().toString());
        return new AuthResponse(token);
    }

    public void signUp(SignUpRequest request) {
        // Check if the user already exists
        Optional<MemberEntity> existingUser = memberRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            MemberEntity user = existingUser.get();

            // Check if the user already has a password (existing user without auth)
            if (user.getPassword() == null) {
                // Update the existing user with a password and role
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRoles(Collections.singletonList("ROLE_USER")); // Default role
                memberRepository.save(user);
            } else {
                throw new RuntimeException("User already exists.");
            }
        }

        // New user: create and save
        MemberEntity newUser = new MemberEntity();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(Collections.singletonList("ROLE_USER")); // Default role

        memberRepository.save(newUser);
    }
}

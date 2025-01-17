package com.mongo.challenge.kitchensink.security;

import com.mongo.challenge.kitchensink.entity.MemberEntity;
import com.mongo.challenge.kitchensink.repo.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        userDetailsService = new CustomUserDetailsService(memberRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        MemberEntity member = new MemberEntity();
        member.setEmail("john.doe@example.com");
        member.setPassword("password");
        member.setRoles(List.of("ROLE_USER"));

        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(member));

        var userDetails = userDetailsService.loadUserByUsername("john.doe@example.com");

        assertEquals(member.getEmail(), userDetails.getUsername());
        assertEquals(member.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("john.doe@example.com");
        });
    }
}
package com.mongo.challenge.kitchensink.service.impl;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.dto.MemberDto;
import com.mongo.challenge.kitchensink.entity.MemberEntity;
import com.mongo.challenge.kitchensink.exception.MemberNotFoundException;
import com.mongo.challenge.kitchensink.mapper.MemberMapper;
import com.mongo.challenge.kitchensink.repo.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberMapper memberMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSaveMember() {
        Member member = new Member();
        member.setPassword("password");
        MemberEntity entity = new MemberEntity();
        entity.setEmail("test@example.com");
        entity.setPassword("password");
        entity.setName("John");
        entity.setRoles(Collections.singletonList("ROLE_USER"));
        when(memberMapper.toEntity(member)).thenReturn(entity);
        when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(entity)).thenReturn(entity);
        when(memberMapper.toDto(entity)).thenReturn(member);
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Member savedMember = memberService.saveMember(member);

        assertEquals(member, savedMember);
        assertEquals("encodedPassword", entity.getPassword());
    }

    @Test
    public void testSaveMemberWithAdminRole() {
        Member member = new Member();
        member.setPassword("password");
        MemberEntity entity = new MemberEntity();
        entity.setEmail("admin@example.com");
        entity.setPassword("password");
        entity.setName("Admin");
        entity.setRoles(new ArrayList<>(Collections.singletonList("ROLE_ADMIN")));
        when(memberMapper.toEntity(member)).thenReturn(entity);
        when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(entity)).thenReturn(entity);
        when(memberMapper.toDto(entity)).thenReturn(member);
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "admin@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Member savedMember = memberService.saveMember(member);

        assertEquals(member, savedMember);
        assertEquals("encodedPassword", entity.getPassword());
        assertEquals(2, entity.getRoles().size());
        assertTrue(entity.getRoles().contains("ROLE_USER"));
        assertTrue(entity.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    public void testSaveMemberWithUserRole() {
        Member member = new Member();
        member.setPassword("password");
        MemberEntity entity = new MemberEntity();
        entity.setEmail("user@example.com");
        entity.setPassword("password");
        entity.setName("User");
        entity.setRoles(Collections.singletonList("ROLE_USER"));
        when(memberMapper.toEntity(member)).thenReturn(entity);
        when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(entity)).thenReturn(entity);
        when(memberMapper.toDto(entity)).thenReturn(member);
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "user@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Member savedMember = memberService.saveMember(member);

        assertEquals(member, savedMember);
        assertEquals("encodedPassword", entity.getPassword());
        assertEquals(1, entity.getRoles().size());
        assertTrue(entity.getRoles().contains("ROLE_USER"));
    }

    @Test
    public void testSaveMemberThrowsException() {
        Member member = new Member();
        member.setPassword("password");

        MemberEntity entity = new MemberEntity();
        entity.setEmail("user@example.com");
        entity.setPassword("password");
        entity.setName("User");
        entity.setRoles(Collections.singletonList("ROLE_USER"));
        when(memberMapper.toEntity(member)).thenReturn(entity);
        when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(entity)).thenThrow(new DuplicateKeyException("Database error"));

        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            memberService.saveMember(member);
        });

        assertEquals("Database error", exception.getMessage());

    }

    @Test
    public void testGetMemberById() {
        MemberEntity entity = new MemberEntity();
        entity.setEmail("test@example.com");
        Member member = new Member();
        when(memberRepository.findByEmail("1")).thenReturn(Optional.of(entity));
        when(memberMapper.toDto(entity)).thenReturn(member);
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Member foundMember = memberService.getMemberById("1");

        assertEquals(member, foundMember);
    }

    @Test
    public void testGetMemberByIdByAdminRole() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setEmail("test@example.com");
        when(memberRepository.findByEmail("1")).thenReturn(Optional.of(memberEntity));
        Member member = new Member();
        when(memberMapper.toDto(memberEntity)).thenReturn(member);

        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "sam@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        Member foundMember = memberService.getMemberById("1");
        assertEquals(member, foundMember);
    }

    @Test
    public void testGetMemberByIdNotFound() {
        when(memberRepository.findByEmail("1")).thenReturn(Optional.empty());
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById("1"));
    }

    @Test
    public void testGetMemberByIdAccessDenied() {
        MemberEntity entity = new MemberEntity();
        entity.setEmail("test@example.com");
        when(memberRepository.findByEmail("1")).thenReturn(Optional.of(entity));
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "sam@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            memberService.getMemberById("1");
        });

        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    public void testUpdateMember() {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("test@example.com");
        memberDto.setName("Updated Name");
        memberDto.setPhoneNumber("1234567890");

        MemberEntity entity = new MemberEntity();
        entity.setEmail("test@example.com");
        entity.setName("Old Name");
        entity.setPhoneNumber("0987654321");

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(entity));
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        memberService.updateMember(memberDto);

        assertEquals("Updated Name", entity.getName());
        assertEquals("1234567890", entity.getPhoneNumber());
        verify(memberRepository).save(entity);
    }

    @Test
    public void testUpdateMemberNotFound() {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("nonexistent@example.com");

        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberDto));
    }

    @Test
    public void testUpdateMemberAccessDenied() {
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("test@example.com");
        memberDto.setName("Updated Name");
        memberDto.setPhoneNumber("1234567890");

        MemberEntity entity = new MemberEntity();
        entity.setEmail("test@example.com");
        entity.setName("Old Name");
        entity.setPhoneNumber("0987654321");

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(entity));
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "otheruser@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class, () -> memberService.updateMember(memberDto));
    }

    @ParameterizedTest
    @MethodSource("provideGetMembersTestCases")
    public void testGetMembers(List<MemberEntity> repositoryResponse, List<Member> expectedResponse) {
        // Arrange
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "test@example.com",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(memberRepository.findAll()).thenReturn(repositoryResponse);
        if (!repositoryResponse.isEmpty()) {
            MemberEntity entity = repositoryResponse.getFirst();
            when(memberMapper.toDto(entity)).thenReturn(expectedResponse.getFirst());
        }

        // Act
        List<Member> members = memberService.getMembers();

        // Assert
        assertEquals(expectedResponse.size(), members.size());
        if (!expectedResponse.isEmpty()) {
            assertEquals(expectedResponse.getFirst(), members.getFirst());
        }
    }

    private static Stream<Arguments> provideGetMembersTestCases() {
        MemberEntity entity = new MemberEntity();
        entity.setEmail("sam@example.com");
        Member member = new Member();
        member.setEmail("sam@example.com");

        return Stream.of(
                // Test case 1: Single member
                Arguments.of(List.of(entity), List.of(member)),

                // Test case 2: Empty response
                Arguments.of(Collections.emptyList(), Collections.emptyList())
        );
    }
}

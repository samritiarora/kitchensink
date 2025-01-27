package com.mongo.challenge.kitchensink.service.impl;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.dto.MemberDto;
import com.mongo.challenge.kitchensink.entity.MemberEntity;
import com.mongo.challenge.kitchensink.exception.MemberNotFoundException;
import com.mongo.challenge.kitchensink.mapper.MemberMapper;
import com.mongo.challenge.kitchensink.repo.MemberRepository;
import com.mongo.challenge.kitchensink.service.IMemberService;
import com.mongo.challenge.kitchensink.util.Util;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService implements IMemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member saveMember(Member member) {
        MemberEntity entity = memberMapper.toEntity(member);
        if (entity.getRoles().size() == 1 && entity.getRoles().contains("ROLE_ADMIN")) {
            entity.getRoles().add("ROLE_USER");
        }
        entity.setPassword(passwordEncoder.encode(member.getPassword()));
        MemberEntity memberEntity = memberRepository.save(entity);
        return memberMapper.toDto(memberEntity);
    }

    @Override
    public List<Member> getMembers() {
        String currentUser = Util.getUser();
        return memberRepository.findAll().stream().filter(user -> !user.getEmail().equalsIgnoreCase(currentUser))
                .map(memberMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Member getMemberById(String id) {
        MemberEntity memberEntity = memberRepository.findByEmail(id).
                orElseThrow(MemberNotFoundException::new);

        if (!Util.getAuthorities().contains("ROLE_ADMIN") &&
                !memberEntity.getEmail().equals(Util.getUser())) {
            throw new AccessDeniedException("Access denied");
        }
        return memberMapper.toDto(memberEntity);
    }

    @Override
    public void updateMember(MemberDto member) {
        MemberEntity memberEntity = memberRepository.findByEmail(member.getEmail()).
                orElseThrow(MemberNotFoundException::new);

        if (!Util.getAuthorities().contains("ROLE_ADMIN") &&
                !memberEntity.getEmail().equals(Util.getUser())) {
            throw new AccessDeniedException("Access denied");
        }

        memberEntity.setName(member.getName());
        memberEntity.setPhoneNumber(member.getPhoneNumber());
        memberRepository.save(memberEntity);
    }

    @Override
    public void deleteMember(String email) {
        memberRepository.deleteByEmail(email);
    }

}

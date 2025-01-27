package com.mongo.challenge.kitchensink.service;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.dto.MemberDto;

import java.util.List;

public interface IMemberService {
    Member saveMember(Member member);

    List<Member> getMembers();

    Member getMemberById(String id);

    void updateMember(MemberDto member);

    void deleteMember(String email);
}

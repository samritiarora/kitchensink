package com.mongo.challenge.kitchensink.mapper;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MemberMapperTest {

    private final MemberMapper mapper = Mappers.getMapper(MemberMapper.class);

    @Test
    public void testToEntity() {
        Member member = new Member();
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");

        MemberEntity entity = mapper.toEntity(member);

        assertNull(entity.getId());
        assertNull(entity.getPassword());
        assertEquals(member.getName(), entity.getName());
        assertEquals(member.getEmail(), entity.getEmail());
    }

    @Test
    public void testToDto() {
        MemberEntity entity = new MemberEntity();
        entity.setId("123");
        entity.setName("John Doe");
        entity.setEmail("john.doe@example.com");

        Member member = mapper.toDto(entity);

        assertEquals(entity.getId(), member.getId());
        assertEquals(entity.getName(), member.getName());
        assertEquals(entity.getEmail(), member.getEmail());
    }
}
package com.mongo.challenge.kitchensink.mapper;

import com.mongo.challenge.kitchensink.dto.Member;
import com.mongo.challenge.kitchensink.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    MemberEntity toEntity(Member member);

    Member toDto(MemberEntity memberEntity);
}

package com.mongo.challenge.kitchensink.repo;

import com.mongo.challenge.kitchensink.entity.MemberEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<MemberEntity, String> {
    Optional<MemberEntity> findByEmail(String email);
}

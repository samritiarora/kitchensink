package com.mongo.challenge.kitchensink.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "member")
public class MemberEntity {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String phoneNumber;
    private List<String> roles;
    private String password;
}

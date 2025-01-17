package com.mongo.challenge.kitchensink.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}

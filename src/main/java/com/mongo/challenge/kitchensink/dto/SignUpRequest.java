package com.mongo.challenge.kitchensink.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password; // Only required if the user is new
}

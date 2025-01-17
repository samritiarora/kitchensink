package com.mongo.challenge.kitchensink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class Member {
    private String id;

    @NotBlank
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[+]?\\d{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private List<String> roles;

    private CharSequence password;
}

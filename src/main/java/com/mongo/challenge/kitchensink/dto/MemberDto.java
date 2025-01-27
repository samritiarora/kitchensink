package com.mongo.challenge.kitchensink.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberDto {
    private String id;
    @NotBlank
    @Size(min = 3, max = 25)
    @Pattern(regexp = "[a-zA-Z]*", message = "Must not contain numbers")
    private String name;

//    @NotBlank
//    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String phoneNumber;
}

package com.mongo.challenge.kitchensink.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String statusCode;
    private String message;
    Map<String, String> errors;
}

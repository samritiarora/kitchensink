package com.mongo.challenge.kitchensink.exception;

import com.mongo.challenge.kitchensink.dto.ErrorResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleDuplicateKeyException(DuplicateKeyException ex) {
        return ErrorResponse.builder().statusCode("CONFLICT_DUPLICATE").message(extractMessageFromException(ex))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract field-specific error messages
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ErrorResponse.builder().statusCode("VALIDATION_ERROR").message("Validation Errors")
                .errors(errors).build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public @ResponseBody ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        return ErrorResponse.builder().statusCode("INVALID_CREDENTIALS")
                .message("The username or password is incorrect").build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ErrorResponse.builder().statusCode("USER_NOT_FOUND").message(ex.getMessage()).build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return ErrorResponse.builder().statusCode("ACCESS_DENIED")
                .message("You do not have permission to access this resource.").build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleGlobalException(Exception ex) {
        return ErrorResponse.builder().statusCode("INTERNAL_ERROR").message("An unexpected error occurred.")
                .build();
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private String extractMessageFromException(DuplicateKeyException ex) {
        String message = ex.getMessage();
        if (message.contains("email")) {
            return "The email address is already in use.";
        } else if (message.contains("phoneNumber")) {
            return "The phone number is already in use.";
        }
        return "A duplicate value exists.";
    }
}

package com.mongo.challenge.kitchensink.exception;

import com.mongo.challenge.kitchensink.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void testHandleDuplicateKeyException1() {
        DuplicateKeyException ex = new DuplicateKeyException("Duplicate key error: email");
        ErrorResponse response = handler.handleDuplicateKeyException(ex);

        assertEquals("CONFLICT_DUPLICATE", response.getStatusCode());
        assertEquals("The email address is already in use.", response.getMessage());
    }

    @Test
    public void testHandleValidationExceptions() {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ErrorResponse response = handler.handleValidationExceptions(ex);

        assertEquals("VALIDATION_ERROR", response.getStatusCode());
        assertEquals("Validation Errors", response.getMessage());
        assertEquals("defaultMessage", response.getErrors().get("field"));
    }

    @Test
    public void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ErrorResponse response = handler.handleBadCredentialsException(ex);

        assertEquals("INVALID_CREDENTIALS", response.getStatusCode());
        assertEquals("The username or password is incorrect", response.getMessage());
    }

    @Test
    public void testHandleUsernameNotFoundException() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");
        ErrorResponse response = handler.handleUsernameNotFoundException(ex);

        assertEquals("USER_NOT_FOUND", response.getStatusCode());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ErrorResponse response = handler.handleAccessDeniedException(ex);

        assertEquals("ACCESS_DENIED", response.getStatusCode());
        assertEquals("You do not have permission to access this resource.", response.getMessage());
    }

    @Test
    public void testHandleGlobalException() {
        Exception ex = new Exception("Global exception");
        ErrorResponse response = handler.handleGlobalException(ex);

        assertEquals("INTERNAL_ERROR", response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getMessage());
    }

    @Test
    public void testHandleMemberNotFoundException() {
        MemberNotFoundException ex = new MemberNotFoundException("Member not found");
        ResponseEntity<String> response = handler.handleMemberNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Member not found", response.getBody());
    }
}
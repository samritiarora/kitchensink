package com.mongo.challenge.kitchensink.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleValidationExceptions() {
        // Create the BindingResult with an error.
        BindingResult bindingResult = new BindException(new Object(), "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "defaultMessage"));

        // Use a mock MethodParameter as the second argument.
        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);

        // Mock the getExecutable method to return a non-null value.
        Mockito.when(methodParameter.getExecutable()).thenReturn(Object.class.getConstructors()[0]);

        // Create the MethodArgumentNotValidException with a valid constructor.
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Call the handler method with the exception.
        ModelAndView modelAndView = globalExceptionHandler.handleValidationExceptions(ex);

        // Assert the results.
        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(HttpStatus.BAD_REQUEST.value(), modelAndView.getModel().get("status"));
        assertEquals("Validation Errors", modelAndView.getModel().get("error"));
        assertEquals("Validation failed", modelAndView.getModel().get("message"));
        assertEquals(Collections.singletonMap("field", "defaultMessage"), modelAndView.getModel().get("validationErrors"));
    }

    @Test
    public void testHandleUsernameNotFoundException() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");
        ModelAndView modelAndView = globalExceptionHandler.handleUsernameNotFoundException(ex);

        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(HttpStatus.NOT_FOUND.value(), modelAndView.getModel().get("status"));
        assertEquals("User Not Found", modelAndView.getModel().get("error"));
        assertEquals("User not found", modelAndView.getModel().get("message"));
    }

    @Test
    public void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ModelAndView modelAndView = globalExceptionHandler.handleBadCredentialsException(ex);

        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), modelAndView.getModel().get("status"));
        assertEquals("Invalid Credentials", modelAndView.getModel().get("error"));
        assertEquals("The username or password is incorrect", modelAndView.getModel().get("message"));
    }

    @Test
    public void testHandleMemberNotFoundException() {
        MemberNotFoundException ex = new MemberNotFoundException("Member not found");
        ModelAndView modelAndView = globalExceptionHandler.handleMemberNotFoundException(ex);

        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(HttpStatus.NOT_FOUND.value(), modelAndView.getModel().get("status"));
        assertEquals("Member Not Found", modelAndView.getModel().get("error"));
        assertEquals("Member not found", modelAndView.getModel().get("message"));
    }

    @Test
    public void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ModelAndView modelAndView = globalExceptionHandler.handleAccessDeniedException(ex);

        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(HttpStatus.FORBIDDEN, modelAndView.getModel().get("status"));
        assertEquals("Access Denied", modelAndView.getModel().get("error"));
        assertEquals("Access denied", modelAndView.getModel().get("message"));
    }

    @Test
    public void testHandleGenericException() {
        Exception ex = new Exception("Generic error");
        ModelAndView modelAndView = globalExceptionHandler.handleGenericException(ex);

        assertNotNull(modelAndView);
        assertEquals("error", modelAndView.getViewName());
        assertEquals(500, modelAndView.getModel().get("status"));
        assertEquals("Internal Server Error", modelAndView.getModel().get("error"));
        assertEquals("Generic error", modelAndView.getModel().get("message"));
    }
}

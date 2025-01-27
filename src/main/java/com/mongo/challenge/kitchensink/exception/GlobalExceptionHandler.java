package com.mongo.challenge.kitchensink.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsernameNotFoundException.class)
    public ModelAndView handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.error("Username not found exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("error", "User Not Found");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", "/"); // Optionally include the request path where the error occurred
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.addObject("error", "Validation Errors");

        // Extract field-specific error messages
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        modelAndView.addObject("message", "Validation failed");
        modelAndView.addObject("validationErrors", errors); // Display validation errors
        modelAndView.addObject("path", "/"); // Optionally include the request path where the error occurred
        return modelAndView;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ModelAndView handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Bad credentials exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.UNAUTHORIZED.value());
        modelAndView.addObject("error", "Invalid Credentials");
        modelAndView.addObject("message", "The username or password is incorrect");
        modelAndView.addObject("path", "/auth/login"); // Path to the login page
        return modelAndView;
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ModelAndView handleMemberNotFoundException(MemberNotFoundException ex) {
        logger.error("Member not found exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("error"); // Points to error.html or error.jsp
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("error", "Member Not Found");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", "/members"); // Include the path where the exception occurred
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied exception occurred", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN);
        modelAndView.addObject("error", "Access Denied");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", "/");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        logger.error("Exception during execution of application", ex);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", 500);
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", "/"); // Optionally include the current path
        return modelAndView;
    }
}

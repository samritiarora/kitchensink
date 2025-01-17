package com.mongo.challenge.kitchensink.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("Member not found");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
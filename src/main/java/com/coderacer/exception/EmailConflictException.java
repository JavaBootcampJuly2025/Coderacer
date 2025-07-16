package com.coderacer.exception;

public class EmailConflictException extends ConflictException {
    public EmailConflictException(String email) {
        super("Email already in use: " + email);
    }
}

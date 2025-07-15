package com.coderacer.exception;

public class UsernameConflictException extends ConflictException {
    public UsernameConflictException(String username) {
        super("Username already in use: " + username);
    }
}

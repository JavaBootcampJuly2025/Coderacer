package com.coderacer.exception;

public class PasswordVerificationException extends RuntimeException {
    public PasswordVerificationException() {
        super("Current password verification failed");
    }
}
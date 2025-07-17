package com.coderacer.exception;

public class EmailNotVerifiedException  extends ConflictException {
    public EmailNotVerifiedException(String message) { super(message); }
}

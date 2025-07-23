package com.coderacer.exception;

public class CodeExecutionClientException extends RuntimeException {
    public CodeExecutionClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
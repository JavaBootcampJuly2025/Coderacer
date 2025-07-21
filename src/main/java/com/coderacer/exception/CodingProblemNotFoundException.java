package com.coderacer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CodingProblemNotFoundException extends RuntimeException {

    public CodingProblemNotFoundException(String message) {
        super(message);
    }

    public CodingProblemNotFoundException(UUID problemId) {
        super("Coding problem not found with ID: " + problemId);
    }

    public CodingProblemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
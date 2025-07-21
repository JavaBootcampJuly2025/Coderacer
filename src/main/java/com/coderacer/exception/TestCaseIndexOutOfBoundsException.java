package com.coderacer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TestCaseIndexOutOfBoundsException extends RuntimeException {

    public TestCaseIndexOutOfBoundsException(String message) {
        super(message);
    }

    public TestCaseIndexOutOfBoundsException(int index, int totalTestCases) {
        super(String.format("Test case index %d is out of bounds. Problem has %d test cases (valid indices: 0-%d)",
                index, totalTestCases, totalTestCases - 1));
    }

    public TestCaseIndexOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
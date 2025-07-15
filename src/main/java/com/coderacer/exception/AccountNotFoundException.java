package com.coderacer.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID id) {
        super("Account not found with ID: " + id);
    }

    public AccountNotFoundException(String username) {
        super("Account not found with username: " + username);
    }
}
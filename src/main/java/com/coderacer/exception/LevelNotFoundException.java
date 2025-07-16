package com.coderacer.exception;

import java.util.UUID;

/**
 * Exception thrown when a Level entity cannot be found by ID or name.
 */
public class LevelNotFoundException extends RuntimeException {

    /**
     * Creates a new exception for missing level with its UUID.
     */
    public LevelNotFoundException(UUID levelId) {
        super("Level not found with ID: " + levelId);
    }

    /**
     * Creates a new exception for missing level with its name.
     */
    public LevelNotFoundException(String levelName) {
        super("Level not found with name: " + levelName);
    }
}

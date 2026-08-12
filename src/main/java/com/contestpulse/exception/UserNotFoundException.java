package com.contestpulse.exception;

/**
 * Thrown when a requested user id doesn't exist. Caught in the controller
 * layer and turned into an HTTP 404 -- same pattern as ContestFetchException
 * from Phase 1 (unchecked, caught explicitly at the controller, no global
 * exception-handling framework).
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}
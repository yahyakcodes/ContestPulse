package com.contestpulse.exception;

/**
 * Thrown when a requested user does not exist.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}
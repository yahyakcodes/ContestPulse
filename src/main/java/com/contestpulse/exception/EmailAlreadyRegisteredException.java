package com.contestpulse.exception;

/**
 * Thrown when POST /api/users is called with an email that's already
 * registered. The User entity has a unique constraint on email, but we
 * check proactively in UserService so the client gets a clear 409 Conflict
 * instead of a raw database constraint-violation error.
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("A user with email '" + email + "' is already registered");
    }
}
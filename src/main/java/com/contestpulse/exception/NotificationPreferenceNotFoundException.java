package com.contestpulse.exception;

/**
 * Thrown when a user exists but no notification preferences
 * have been created for that user yet.
 */
public class NotificationPreferenceNotFoundException extends RuntimeException {

    public NotificationPreferenceNotFoundException(Long userId) {
        super("No notification preferences found for user id: " + userId);
    }
}
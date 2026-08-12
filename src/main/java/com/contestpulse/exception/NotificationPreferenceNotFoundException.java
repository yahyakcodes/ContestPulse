package com.contestpulse.exception;

/**
 * Thrown by GET /api/users/{userId}/preferences when the user exists but
 * hasn't had preferences created yet (no PUT has happened for them).
 * Kept distinct from UserNotFoundException so the two 404 cases ("user
 * doesn't exist" vs "user exists, preferences don't yet") can be told apart
 * from the error message and, if needed, handled differently later.
 */
public class NotificationPreferenceNotFoundException extends RuntimeException {

    public NotificationPreferenceNotFoundException(Long userId) {
        super("No notification preferences found for user id: " + userId);
    }
}
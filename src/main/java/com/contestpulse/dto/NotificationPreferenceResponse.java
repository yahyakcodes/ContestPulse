package com.contestpulse.dto;

/**
 * Response body for PUT and GET /api/users/{userId}/preferences.
 */
public record NotificationPreferenceResponse(
        Long id,
        Long userId,
        boolean emailEnabled,
        boolean telegramEnabled,
        boolean whatsappEnabled,
        int reminderMinutesBefore
) {
}
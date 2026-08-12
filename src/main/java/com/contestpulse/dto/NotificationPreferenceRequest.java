package com.contestpulse.dto;

import jakarta.validation.constraints.Min;

/**
 * Request body for PUT /api/users/{userId}/preferences.
 *
 * This is a full-replace PUT: send the complete desired state every time,
 * matching the four fields on the existing NotificationPreference entity.
 */
public record NotificationPreferenceRequest(
        boolean emailEnabled,
        boolean telegramEnabled,
        boolean whatsappEnabled,

        @Min(value = 0, message = "reminderMinutesBefore cannot be negative")
        int reminderMinutesBefore
) {
}
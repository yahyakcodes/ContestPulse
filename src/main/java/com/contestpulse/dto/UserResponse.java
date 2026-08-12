package com.contestpulse.dto;

/**
 * Response body for POST /api/users and GET /api/users/{id}.
 * Returned instead of the User entity directly so the API's shape doesn't
 * change just because the entity gains internal-only fields later.
 */
public record UserResponse(
        Long id,
        String email,
        String timezone,
        String telegramChatId,
        String whatsappNumber
) {
}
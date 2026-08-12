package com.contestpulse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/users.
 *
 * telegramChatId and whatsappNumber are intentionally NOT part of this
 * request: per the existing User entity's own comments, those are filled in
 * later by the Telegram/WhatsApp channel-linking flows (Phase 3/4+), not by
 * generic registration.
 */
public record CreateUserRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        String email,

        // Optional. IANA timezone name (e.g. "Asia/Kolkata"). If blank or
        // omitted, the User entity's own default ("Asia/Kolkata") is used.
        String timezone
) {
}
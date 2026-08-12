package com.contestpulse.controller;

import com.contestpulse.dto.NotificationPreferenceRequest;
import com.contestpulse.dto.NotificationPreferenceResponse;
import com.contestpulse.service.NotificationPreferenceNotFoundException;
import com.contestpulse.service.NotificationPreferenceService;
import com.contestpulse.service.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Kept as its own controller (rather than folded into UserController)
 * because it's really about the NotificationPreference resource, nested
 * under a user -- same one-controller-per-resource pattern Phase 1 used
 * for ContestController.
 */
@Slf4j
@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService notificationPreferenceService;

    public NotificationPreferenceController(NotificationPreferenceService notificationPreferenceService) {
        this.notificationPreferenceService = notificationPreferenceService;
    }

    @PutMapping
    public ResponseEntity<?> upsertPreferences(@PathVariable Long userId,
                                               @Valid @RequestBody NotificationPreferenceRequest request) {
        try {
            NotificationPreferenceResponse response =
                    notificationPreferenceService.upsertPreferences(userId, request);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException ex) {
            log.warn("Preference update rejected: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getPreferences(@PathVariable Long userId) {
        try {
            NotificationPreferenceResponse response = notificationPreferenceService.getPreferences(userId);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException | NotificationPreferenceNotFoundException ex) {
            log.warn("Preference lookup failed: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
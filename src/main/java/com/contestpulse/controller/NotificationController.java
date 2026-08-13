package com.contestpulse.controller;

import com.contestpulse.service.ReminderRunResult;
import com.contestpulse.service.ReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Manual trigger for ReminderService -- lets the send+log flow be verified
 * (and demoed) without waiting for the scheduler, which doesn't exist yet.
 * Same "manual trigger before automation exists" pattern Phase 1 used for
 * POST /api/contests/sync before the scheduler existed for contest syncing.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final ReminderService reminderService;

    public NotificationController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping("/run-reminder-check")
    public ResponseEntity<ReminderRunResult> runReminderCheck() {
        ReminderRunResult result = reminderService.runReminderCheck();
        return ResponseEntity.ok(result);
    }
}
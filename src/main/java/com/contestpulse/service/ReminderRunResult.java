package com.contestpulse.service;

/**
 * Outcome of one ReminderService.runReminderCheck() run. Returned by the
 * manual trigger endpoint as JSON, and will also be what the scheduler logs
 * once it exists.
 */
public record ReminderRunResult(
        int contestsConsidered,
        int usersConsidered,
        int remindersSent,
        int remindersSkippedAlreadySent,
        int remindersFailed
) {
}
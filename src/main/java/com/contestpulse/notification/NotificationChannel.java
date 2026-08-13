package com.contestpulse.notification;

import com.contestpulse.model.Contest;
import com.contestpulse.model.NotificationPreference;
import com.contestpulse.model.User;

/**
 * A NotificationChannel knows how to deliver one contest reminder through
 * one medium (email today; Telegram next). Mirrors the ContestProvider
 * pattern from Phase 1: adding a new channel later means writing one new
 * class that implements this interface, not touching ReminderService.
 */
public interface NotificationChannel {

    /**
     * Stable identifier stored in NotificationLog.channel and matched
     * against by NotificationLogRepository's dedup check -- e.g. "EMAIL".
     */
    String getChannelName();

    /**
     * Whether this channel should fire for a given user's preferences.
     * For email this is just the emailEnabled flag; a channel that needs
     * extra setup (e.g. Telegram needing a linked chat id) checks that here
     * too, so ReminderService never needs channel-specific knowledge.
     */
    boolean isEnabledFor(NotificationPreference preference);

    /**
     * Actually sends the reminder. Implementations should throw on failure
     * (not swallow it) -- ReminderService is responsible for catching,
     * logging, and NOT writing a NotificationLog row on failure, so a
     * failed send gets retried on the next run instead of being lost.
     */
    void send(User user, Contest contest);
}
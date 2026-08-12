package com.contestpulse.service;

import com.contestpulse.dto.NotificationPreferenceRequest;
import com.contestpulse.dto.NotificationPreferenceResponse;
import com.contestpulse.model.NotificationPreference;
import com.contestpulse.model.User;
import com.contestpulse.repository.NotificationPreferenceRepository;
import com.contestpulse.repository.UserRepository;
import com.contestpulse.exception.UserNotFoundException;
import com.contestpulse.exception.NotificationPreferenceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationPreferenceService {

    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationPreferenceService(UserRepository userRepository,
                                         NotificationPreferenceRepository notificationPreferenceRepository) {
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    /**
     * Creates preferences for this user if none exist yet, otherwise updates
     * the existing row in place. Because we look up the existing row first
     * and call save() on that same managed entity (same id) when it exists,
     * this can never create a second preferences row for the same user --
     * the (user_id) uniqueness in NotificationPreference is preserved as an
     * UPDATE, not a second INSERT.
     */
    public NotificationPreferenceResponse upsertPreferences(Long userId, NotificationPreferenceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference created = new NotificationPreference();
                    created.setUser(user);
                    return created;
                });

        preference.setEmailEnabled(request.emailEnabled());
        preference.setTelegramEnabled(request.telegramEnabled());
        preference.setWhatsappEnabled(request.whatsappEnabled());
        preference.setReminderMinutesBefore(request.reminderMinutesBefore());

        NotificationPreference saved = notificationPreferenceRepository.save(preference);
        log.info("Upserted notification preferences for userId={} (preferenceId={})", userId, saved.getId());

        return toResponse(saved);
    }

    /**
     * Distinguishes two different 404 cases: the user doesn't exist at all,
     * versus the user exists but hasn't had preferences created yet (no PUT
     * has happened for them). Both currently map to 404 in the controller,
     * but the distinct exceptions/messages make it easy to tell them apart
     * from logs, or handle them differently later.
     */
    public NotificationPreferenceResponse getPreferences(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        NotificationPreference preference = notificationPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new NotificationPreferenceNotFoundException(userId));

        return toResponse(preference);
    }

    private NotificationPreferenceResponse toResponse(NotificationPreference preference) {
        return new NotificationPreferenceResponse(
                preference.getId(),
                preference.getUser().getId(),
                preference.isEmailEnabled(),
                preference.isTelegramEnabled(),
                preference.isWhatsappEnabled(),
                preference.getReminderMinutesBefore()
        );
    }
}
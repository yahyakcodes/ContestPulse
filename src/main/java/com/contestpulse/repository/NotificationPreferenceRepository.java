package com.contestpulse.repository;

import com.contestpulse.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    Optional<NotificationPreference> findByUserId(Long userId);

    // Used by ReminderService, which reads preference.getUser().getEmail()/
    // getTimezone() after this call returns -- JOIN FETCH loads the user
    // eagerly in this one query, avoiding a LazyInitializationException that
    // plain findAll() would cause once the session closes.
    @Query("SELECT np FROM NotificationPreference np JOIN FETCH np.user")
    List<NotificationPreference> findAllWithUser();
}
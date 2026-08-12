package com.contestpulse.repository;

import com.contestpulse.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    boolean existsByUserIdAndContestIdAndChannel(Long userId, Long contestId, String channel);
}

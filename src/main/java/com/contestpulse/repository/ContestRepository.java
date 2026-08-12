package com.contestpulse.repository;

import com.contestpulse.model.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Long> {

    // used by every ContestProvider before inserting, so re-fetching never creates duplicates
    Optional<Contest> findByPlatformAndExternalId(String platform, String externalId);

    // used by the frontend's "upcoming contests" list
    List<Contest> findByStartTimeUtcAfterOrderByStartTimeUtcAsc(Instant now);

    // used by the Phase 4 scheduler to find contests starting soon
    List<Contest> findByStartTimeUtcBetweenOrderByStartTimeUtcAsc(Instant from, Instant to);
}

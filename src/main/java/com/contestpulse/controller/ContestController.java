package com.contestpulse.controller;

import com.contestpulse.model.Contest;
import com.contestpulse.provider.ContestFetchException;
import com.contestpulse.repository.ContestRepository;
import com.contestpulse.service.ContestSyncResult;
import com.contestpulse.service.ContestSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Thin controller: no business logic here, just translates HTTP requests
 * into service/repository calls and results into HTTP responses.
 *
 * GET /api/contests returns Contest entities directly rather than a separate
 * response DTO -- a deliberate simplification for Phase 1, since Contest has
 * no sensitive fields and no lazy-loaded relationships to worry about
 * (unlike, say, NotificationLog, which references User). Worth revisiting
 * with a proper response DTO if Contest ever gains internal-only fields.
 */
@Slf4j
@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestSyncService contestSyncService;
    private final ContestRepository contestRepository;

    public ContestController(ContestSyncService contestSyncService, ContestRepository contestRepository) {
        this.contestSyncService = contestSyncService;
        this.contestRepository = contestRepository;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncContests() {
        try {
            ContestSyncResult result = contestSyncService.syncContests();
            return ResponseEntity.ok(result);
        } catch (ContestFetchException ex) {
            // Fail loudly and clearly instead of returning 200 OK with no
            // contests -- a caller (or a future scheduler) needs to be able
            // to tell "sync failed" apart from "sync succeeded, nothing new".
            log.error("Contest sync failed", ex);
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Contest sync failed: " + ex.getMessage()));
        }
    }

    @GetMapping
    public List<Contest> getUpcomingContests() {
        return contestRepository.findByStartTimeUtcAfterOrderByStartTimeUtcAsc(Instant.now());
    }
}

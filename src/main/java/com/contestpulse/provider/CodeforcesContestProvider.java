package com.contestpulse.provider;

import com.contestpulse.client.codeforces.CodeforcesApiResponse;
import com.contestpulse.client.codeforces.CodeforcesContestDto;
import com.contestpulse.model.Contest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.List;

/**
 * ContestProvider implementation for Codeforces.
 *
 * @Component registers this as a Spring bean so it can be constructor-injected
 * wherever a ContestProvider is needed (ContestSyncService today).
 *
 * @Slf4j (Lombok) generates a private static `log` field -- same logging
 * approach used elsewhere is kept consistent by using SLF4J directly rather
 * than System.out.
 */
@Slf4j
@Component
public class CodeforcesContestProvider implements ContestProvider {

    private static final String PLATFORM = "CODEFORCES";
    private static final String UPCOMING_PHASE = "BEFORE";

    private final RestClient codeforcesRestClient;

    // Constructor injection (not field injection): Spring passes in the
    // RestClient bean defined in RestClientConfig. This makes the dependency
    // explicit and lets tests construct this class with a mock RestClient
    // without needing Spring at all.
    public CodeforcesContestProvider(RestClient codeforcesRestClient) {
        this.codeforcesRestClient = codeforcesRestClient;
    }

    @Override
    public List<Contest> fetchContests() {
        CodeforcesApiResponse response = callCodeforcesApi();
        validateResponse(response);

        List<CodeforcesContestDto> upcoming = response.result().stream()
                .filter(dto -> UPCOMING_PHASE.equals(dto.phase()))
                .filter(this::hasStartTime)
                .toList();

        log.info("Codeforces returned {} contests total, {} are upcoming (phase=BEFORE)",
                response.result().size(), upcoming.size());

        return upcoming.stream()
                .map(this::toContest)
                .toList();
    }

    private CodeforcesApiResponse callCodeforcesApi() {
        try {
            return codeforcesRestClient.get()
                    .uri("/contest.list")
                    .retrieve()
                    .body(CodeforcesApiResponse.class);
        } catch (RestClientException ex) {
            // Covers both "couldn't connect at all" (ResourceAccessException)
            // and "Codeforces responded with a 4xx/5xx" (RestClientResponseException).
            // Wrapped so callers only ever need to catch one exception type.
            log.error("Failed to reach Codeforces contest.list API", ex);
            throw new ContestFetchException("Could not reach Codeforces API", ex);
        }
    }

    private void validateResponse(CodeforcesApiResponse response) {
        if (response == null || response.result() == null) {
            throw new ContestFetchException("Codeforces API returned an empty/unreadable response");
        }
        if (!"OK".equals(response.status())) {
            throw new ContestFetchException("Codeforces API returned non-OK status: " + response.status());
        }
    }

    private boolean hasStartTime(CodeforcesContestDto dto) {
        if (dto.startTimeSeconds() == null) {
            // Rare, but Codeforces can list a BEFORE-phase contest with no
            // confirmed start time yet. Skip it rather than crash on the
            // Instant.ofEpochSecond(null) unboxing below.
            log.warn("Skipping Codeforces contest '{}' (id={}) - no startTimeSeconds yet", dto.name(), dto.id());
            return false;
        }
        return true;
    }

    private Contest toContest(CodeforcesContestDto dto) {
        Contest contest = new Contest();
        contest.setPlatform(PLATFORM);
        contest.setExternalId(String.valueOf(dto.id()));
        contest.setName(dto.name());
        contest.setStartTimeUtc(Instant.ofEpochSecond(dto.startTimeSeconds()));
        contest.setDurationSeconds(dto.durationSeconds());
        contest.setUrl("https://codeforces.com/contest/" + dto.id());
        return contest;
    }
}

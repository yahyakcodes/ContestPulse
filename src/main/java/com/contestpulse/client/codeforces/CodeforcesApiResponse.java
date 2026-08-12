package com.contestpulse.client.codeforces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Mirrors the top-level envelope every Codeforces API call returns:
 *
 *   { "status": "OK", "result": [ ... ] }
 *   { "status": "FAILED", "comment": "..." }
 *
 * Not made generic (e.g. CodeforcesApiResponse<T>) on purpose -- ContestPulse
 * only ever calls contest.list right now, so a response type specific to
 * CodeforcesContestDto is simpler than a generic wrapper we don't need yet.
 * If a second Codeforces endpoint is added later, this can be generified then.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CodeforcesApiResponse(
        String status,
        List<CodeforcesContestDto> result
) {
}

package com.contestpulse.client.codeforces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Mirrors one "Contest" object from the Codeforces API (contest.list method).
 * See: https://codeforces.com/apiHelp/objects#Contest
 *
 * This intentionally only declares the fields ContestPulse actually needs.
 * Codeforces sends more fields (type, relativeTimeSeconds, preparedBy, ...) --
 * @JsonIgnoreProperties(ignoreUnknown = true) tells Jackson to silently drop
 * anything we didn't list here instead of failing deserialization.
 *
 * A Java record is used because this is a plain, immutable "data holder" --
 * Jackson (2.12+, bundled with Spring Boot 3.5) deserializes JSON straight
 * into records via their canonical constructor, so no Lombok/no-args
 * constructor/setters are needed here the way they are on JPA entities.
 *
 * startTimeSeconds is a boxed Long (not a primitive long) because Codeforces
 * can omit it for contests that don't have a confirmed start time yet -- a
 * primitive long can't represent "absent", a boxed Long can (it's just null).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CodeforcesContestDto(
        long id,
        String name,
        String phase,
        Long startTimeSeconds,
        long durationSeconds
) {
}

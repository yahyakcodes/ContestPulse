package com.contestpulse.provider;

import com.contestpulse.model.Contest;

import java.util.List;

/**
 * A ContestProvider knows how to talk to one external contest platform
 * (Codeforces today; LeetCode/CodeChef/etc. could each get their own
 * implementation later) and hand back ready-to-save Contest entities.
 *
 * This is exactly the abstraction the Contest entity's "platform" field
 * comment already anticipated: adding a new platform later means writing
 * one new class that implements this interface -- ContestSyncService and
 * everything downstream of it does not need to change.
 */
public interface ContestProvider {

    /**
     * Fetches contests from this provider's platform and maps them into
     * Contest entities. Implementations should only return contests that
     * are ready to be persisted (platform, externalId, name, startTimeUtc,
     * durationSeconds, url all populated).
     *
     * @throws ContestFetchException if the underlying source could not be
     *         reached or returned an unsuccessful response.
     */
    List<Contest> fetchContests();
}

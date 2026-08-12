package com.contestpulse.service;

/**
 * Outcome of one ContestSyncService.syncContests() run.
 * Jackson serializes this record straight to JSON (fetched/newlySaved/alreadyExisted)
 * when ContestController returns it from POST /api/contests/sync.
 */
public record ContestSyncResult(
        int fetched,
        int newlySaved,
        int alreadyExisted
) {
}

package com.contestpulse.provider;

/**
 * Thrown by any ContestProvider implementation when it cannot successfully
 * fetch contests -- network failure, an unreachable API, or a non-"OK"
 * status in the response body.
 *
 * Unchecked (extends RuntimeException) so ContestSyncService and
 * ContestController don't need to declare/catch it everywhere; the
 * controller catches it in one place and turns it into an HTTP error
 * response instead of letting the sync silently "succeed" with zero contests.
 */
public class ContestFetchException extends RuntimeException {

    public ContestFetchException(String message) {
        super(message);
    }

    public ContestFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}

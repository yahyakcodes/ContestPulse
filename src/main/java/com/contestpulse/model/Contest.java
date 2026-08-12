package com.contestpulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(
        name = "contests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"platform", "external_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // "CODEFORCES" today; "LEETCODE" / "GFG" get added later just by a new
    // ContestProvider writing that string here -- this column never changes
    @Column(nullable = false)
    private String platform;

    // the platform's own contest id -- combined with platform, this is how
    // we detect "have I already stored this contest" on every re-fetch
    @Column(name = "external_id", nullable = false)
    private String externalId;

    // always UTC in the database; converted to the user's timezone in the service layer
    @Column(name = "start_time_utc", nullable = false)
    private Instant startTimeUtc;

    @Column(name = "duration_seconds", nullable = false)
    private long durationSeconds;

    @Column(nullable = false)
    private String url;
}

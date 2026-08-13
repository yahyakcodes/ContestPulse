package com.contestpulse.service;

import com.contestpulse.model.Contest;
import com.contestpulse.model.NotificationLog;
import com.contestpulse.model.NotificationPreference;
import com.contestpulse.model.User;
import com.contestpulse.notification.NotificationChannel;
import com.contestpulse.repository.ContestRepository;
import com.contestpulse.repository.NotificationLogRepository;
import com.contestpulse.repository.NotificationPreferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Finds (user, contest) pairs that should get a reminder and sends it
 * through NotificationChannel, deduplicating via NotificationLogRepository
 * so the same user/contest/channel is never notified twice.
 *
 * Only one NotificationChannel exists today (EmailNotificationChannel), so
 * it's injected directly by interface type rather than as
 * List<NotificationChannel> -- same reasoning already used for
 * ContestSyncService/ContestProvider: no second implementation exists yet,
 * so there's nothing to loop over. This constructor will change to accept
 * List<NotificationChannel> when TelegramNotificationChannel is added next.
 *
 * NOT yet filtering by NotificationPreference.reminderMinutesBefore: "when
 * to check the clock" is the scheduler's job, which doesn't exist yet.
 * Right now this sends a reminder for every upcoming contest a user hasn't
 * already been notified about on this channel -- intentional for testing
 * the send+log flow manually before the scheduler exists. This will change
 * when the scheduler is added (next).
 *
 * No @Transactional: sending an email is a network call (SMTP), and a
 * @Transactional method would hold a DB connection open across it. Each
 * repository call below is already transactional on its own via Spring
 * Data JPA defaults.
 */
@Slf4j
@Service
public class ReminderService {

    private final ContestRepository contestRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationChannel notificationChannel;

    public ReminderService(ContestRepository contestRepository,
                           NotificationPreferenceRepository notificationPreferenceRepository,
                           NotificationLogRepository notificationLogRepository,
                           NotificationChannel notificationChannel) {
        this.contestRepository = contestRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.notificationChannel = notificationChannel;
    }

    public ReminderRunResult runReminderCheck() {
        Instant now = Instant.now();
        List<Contest> upcomingContests = contestRepository.findByStartTimeUtcAfterOrderByStartTimeUtcAsc(now);
        // findAllWithUser() (not findAll()) -- eagerly joins the lazy `user`
        // association so user.getEmail()/getTimezone() below don't throw
        // LazyInitializationException after this repository call's own
        // transaction/session has already closed.
        List<NotificationPreference> preferences = notificationPreferenceRepository.findAllWithUser();

        int sent = 0;
        int alreadySent = 0;
        int failed = 0;

        for (NotificationPreference preference : preferences) {
            if (!notificationChannel.isEnabledFor(preference)) {
                continue;
            }

            User user = preference.getUser();

            for (Contest contest : upcomingContests) {
                boolean alreadyLogged = notificationLogRepository.existsByUserIdAndContestIdAndChannel(
                        user.getId(), contest.getId(), notificationChannel.getChannelName());

                if (alreadyLogged) {
                    alreadySent++;
                    continue;
                }

                try {
                    notificationChannel.send(user, contest);

                    NotificationLog notificationLog = new NotificationLog();
                    notificationLog.setUser(user);
                    notificationLog.setContest(contest);
                    notificationLog.setChannel(notificationChannel.getChannelName());
                    notificationLog.setSentAt(Instant.now());
                    notificationLogRepository.save(notificationLog);

                    sent++;
                    log.info("Sent {} reminder to userId={} for contestId={}",
                            notificationChannel.getChannelName(), user.getId(), contest.getId());
                } catch (Exception ex) {
                    failed++;
                    log.error("Failed to send {} reminder to userId={} for contestId={}",
                            notificationChannel.getChannelName(), user.getId(), contest.getId(), ex);
                    // Deliberately not saving a NotificationLog row here --
                    // the next run will retry, since nothing was logged.
                }
            }
        }

        log.info("Reminder check complete: contests={}, users={}, sent={}, alreadySent={}, failed={}",
                upcomingContests.size(), preferences.size(), sent, alreadySent, failed);

        return new ReminderRunResult(upcomingContests.size(), preferences.size(), sent, alreadySent, failed);
    }
}
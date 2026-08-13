package com.contestpulse.notification;

import com.contestpulse.model.Contest;
import com.contestpulse.model.NotificationPreference;
import com.contestpulse.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Sends a contest reminder by email using Spring's JavaMailSender, which
 * spring-boot-starter-mail auto-configures once spring.mail.host is set in
 * application.properties -- no manual @Bean needed for JavaMailSender itself.
 */
@Slf4j
@Component
public class EmailNotificationChannel implements NotificationChannel {

    private static final String CHANNEL_NAME = "EMAIL";

    private final JavaMailSender mailSender;
    private final String fromAddress;

    // fromAddress is read from spring.mail.username rather than a separate
    // property: Gmail's SMTP relay (the default host) requires the From
    // address to match the authenticated account anyway, so a second
    // "from" property would just be one more thing that has to stay in
    // sync with MAIL_USERNAME.
    public EmailNotificationChannel(JavaMailSender mailSender,
                                    @Value("${spring.mail.username:}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public String getChannelName() {
        return CHANNEL_NAME;
    }

    @Override
    public boolean isEnabledFor(NotificationPreference preference) {
        return preference.isEmailEnabled();
    }

    @Override
    public void send(User user, Contest contest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Reminder: " + contest.getName() + " starts soon");
        message.setText(
                "Heads up! " + contest.getName() + " starts at " + formatStartTime(user, contest)
                        + " (" + user.getTimezone() + ").\n\n"
                        + "Contest link: " + contest.getUrl() + "\n\n"
                        + "-- ContestPulse"
        );

        // Throws MailException on failure (auth error, connection refused,
        // etc.) -- deliberately not caught here. ReminderService catches it,
        // so a failed send never gets a NotificationLog row written and is
        // retried on the next run.
        mailSender.send(message);
    }

    private String formatStartTime(User user, Contest contest) {
        ZoneId zone;
        try {
            zone = ZoneId.of(user.getTimezone());
        } catch (Exception ex) {
            log.warn("Invalid timezone '{}' for user {}, falling back to UTC", user.getTimezone(), user.getId());
            zone = ZoneId.of("UTC");
        }
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(zone)
                .format(contest.getStartTimeUtc());
    }
}
package com.contestpulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // these three flags exist today even though only email gets built this
    // week -- adding Telegram/WhatsApp later means reading a flag that is
    // already here, not a schema migration
    @Column(nullable = false)
    private boolean emailEnabled = true;

    @Column(nullable = false)
    private boolean telegramEnabled = false;

    @Column(nullable = false)
    private boolean whatsappEnabled = false;

    @Column(nullable = false)
    private int reminderMinutesBefore = 30;
}

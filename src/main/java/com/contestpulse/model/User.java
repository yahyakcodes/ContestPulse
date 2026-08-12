package com.contestpulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // IANA timezone name, e.g. "Asia/Kolkata" -- everything is stored in
    // UTC and converted to this per-user timezone at read time
    @Column(nullable = false)
    private String timezone = "Asia/Kolkata";

    // filled in once Phase 3/4 wires up the Telegram channel; null until then
    private String telegramChatId;

    // filled in only if/when a WhatsApp channel is added later
    private String whatsappNumber;
}

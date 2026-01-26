package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "password_reset_otps", indexes = {
        @Index(name = "idx_otp", columnList = "otp"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_expiry", columnList = "expiry_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetOtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false)
    private int attemptCount = 0;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private String ipAddress;
}
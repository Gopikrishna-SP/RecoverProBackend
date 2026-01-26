package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.PasswordResetOtpEntity;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.PasswordResetOtpRepository;
import com.nimis.chatbot.repository.UserRepository;
import com.nimis.chatbot.utility.OtpGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;
    private final RateLimitService rateLimitService;
    private final AuditService auditService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;

    // ===========================
    // STEP 1: SEND OTP
    // ===========================
    @Transactional
    public void sendPasswordResetOtp(String email, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        // Rate limiting
        if (!rateLimitService.allowRequest("send_otp:" + email, 3, Duration.ofHours(1))) {
            log.warn("Rate limit exceeded for OTP request: {}", email);
            throw new RuntimeException("Too many attempts. Try again later");
        }

        // Check user exists (don't reveal)
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        auditService.log("PASSWORD_RESET_OTP_REQUESTED", email, clientIp);

        if (user == null) {
            log.info("OTP request for non-existent email: {}", email);
            return; // Return success to prevent enumeration
        }

        // Delete old OTPs
        otpRepository.deleteByUserId(user.getId());

        // Generate and save OTP
        String otp = otpGenerator.generateOtp();
        PasswordResetOtpEntity otpEntity = PasswordResetOtpEntity.builder()
                .user(user)
                .otp(otp)
                .expiryDate(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .verified(false)
                .attemptCount(0)
                .ipAddress(clientIp)
                .build();

        otpRepository.save(otpEntity);

        // Send OTP via email
        try {
            emailService.sendOtp(user.getEmail(), otp, OTP_EXPIRY_MINUTES);
            auditService.log("PASSWORD_RESET_OTP_SENT", user.getEmail(), clientIp);
            log.info("OTP sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP to: {}", email, e);
            throw new RuntimeException("Failed to send OTP");
        }
    }

    // ===========================
    // STEP 2: VERIFY OTP
    // ===========================
    @Transactional
    public void verifyOtp(String email, String otp, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find active OTP
        PasswordResetOtpEntity otpEntity = otpRepository
                .findByUserAndOtpAndVerifiedFalse(user, otp)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        // Check expiry
        if (Instant.now().isAfter(otpEntity.getExpiryDate())) {
            auditService.log("PASSWORD_RESET_OTP_EXPIRED", email, clientIp);
            throw new RuntimeException("OTP has expired. Request a new one");
        }

        // Check attempts
        if (otpEntity.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            auditService.log("PASSWORD_RESET_MAX_ATTEMPTS_EXCEEDED", email, clientIp);
            throw new RuntimeException("Maximum attempts exceeded. Request a new OTP");
        }

        // Verify OTP
        if (!otpEntity.getOtp().equals(otp)) {
            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            otpRepository.save(otpEntity);
            auditService.log("PASSWORD_RESET_OTP_INVALID_ATTEMPT", email, clientIp);
            throw new IllegalArgumentException("Invalid OTP");
        }

        // Mark as verified
        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
        auditService.log("PASSWORD_RESET_OTP_VERIFIED", email, clientIp);
    }

    // ===========================
    // STEP 3: RESET PASSWORD
    // ===========================
    @Transactional
    public void resetPassword(String email, String newPassword, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check OTP was verified (must have at least 1 verified OTP)
        long verifiedCount = otpRepository.countVerifiedOtpByUserId(user.getId());
        if (verifiedCount == 0) {
            auditService.log("PASSWORD_RESET_OTP_NOT_VERIFIED", email, clientIp);
            throw new IllegalArgumentException("OTP verification required");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Clear all OTPs for user
        otpRepository.deleteByUserId(user.getId());

        // Audit & notify
        auditService.log("PASSWORD_RESET_SUCCESS", user.getEmail(), clientIp);
        emailService.sendPasswordChangedNotification(user.getEmail());
        log.info("Password reset successful for: {}", email);
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
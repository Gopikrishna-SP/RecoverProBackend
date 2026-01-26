package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.SendOtpRequest;
import com.nimis.chatbot.dto.request.VerifyOtpRequest;
import com.nimis.chatbot.dto.request.ResetPasswordRequest;
import com.nimis.chatbot.dto.response.MessageResponse;
import com.nimis.chatbot.dto.response.OtpResponse;
import com.nimis.chatbot.dto.response.VerifyOtpResponse;
import com.nimis.chatbot.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private static final int OTP_EXPIRY_MINUTES = 10;

    // ===========================
    // STEP 1: Send OTP
    // ===========================
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(
            @Valid @RequestBody SendOtpRequest req,
            HttpServletRequest request) {
        try {
            passwordResetService.sendPasswordResetOtp(req.getEmail(), request);
        } catch (Exception e) {
            log.error("Send OTP error", e);
        }
        // Always return success to prevent email enumeration
        return ResponseEntity.ok(new OtpResponse(
                "OTP sent to your registered email", OTP_EXPIRY_MINUTES * 60));
    }

    // ===========================
    // STEP 2: Verify OTP
    // ===========================
    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest req,
            HttpServletRequest request) {
        try {
            passwordResetService.verifyOtp(req.getEmail(), req.getOtp(), request);
            return ResponseEntity.ok(new VerifyOtpResponse("OTP verified successfully", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerifyOtpResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerifyOtpResponse(e.getMessage(), false));
        }
    }

    // ===========================
    // STEP 3: Reset Password
    // ===========================
    @PostMapping("/reset")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req,
            HttpServletRequest request) {
        try {
            passwordResetService.resetPassword(req.getEmail(), req.getNewPassword(), request);
            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        } catch (Exception e) {
            log.error("Reset password error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }
}
package com.nimis.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.from-name}")
    private String fromName;

    public void sendOtp(String to, String otp, int expiryMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject("Password Reset OTP");

            String htmlContent = buildOtpEmailHtml(otp, expiryMinutes);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent to: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    public void sendPasswordChangedNotification(String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject("Password Changed Successfully");

            String htmlContent = buildPasswordChangedHtml();
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password changed notification sent to: {}", to);

        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send notification email to: {}", to, e);
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private String buildOtpEmailHtml(String otp, int expiryMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 8px; }
                    .header { text-align: center; color: #333; }
                    .otp-box { background: #f0f0f0; padding: 20px; text-align: center; border-radius: 5px; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; }
                    .expiry { color: #666; font-size: 14px; margin-top: 15px; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #ddd; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Reset Your Password</h2>
                    </div>
                    <p>Hi,</p>
                    <p>You requested to reset your password. Use the OTP below:</p>
                    <div class="otp-box">
                        <div class="otp-code">""" + otp + """
                        </div>
                        <div class="expiry">Expires in """ + expiryMinutes + """ 
                    minutes</div>
                    </div>
                    <p><strong>Security:</strong></p>
                    <ul>
                        <li>Never share this OTP</li>
                        <li>Valid for """ + expiryMinutes + """ 
                        minutes only</li>
                        <li>Ignore if you didn't request this</li>
                    </ul>
                    <div class="footer">
                        <p>© 2024 YourApp. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private String buildPasswordChangedHtml() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 20px auto; background: white; padding: 30px; border-radius: 8px; }
                    .header { text-align: center; color: #333; }
                    .success { color: #28a745; font-size: 18px; text-align: center; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #ddd; padding-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header"><h2>Password Changed</h2></div>
                    <div class="success">✓ Your password has been changed successfully</div>
                    <p>Hi,</p>
                    <p>This confirms your password was recently changed.</p>
                    <p><strong>Didn't make this change?</strong> Change your password immediately and contact support.</p>
                    <div class="footer"><p>© 2024 YourApp. All rights reserved.</p></div>
                </div>
            </body>
            </html>
            """;
    }
}
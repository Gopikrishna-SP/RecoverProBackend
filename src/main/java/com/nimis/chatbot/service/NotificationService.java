package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.NotificationResponse;
import com.nimis.chatbot.model.entity.*;
import com.nimis.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    /**
     * Send notification to ALL users
     */
    @Transactional
    public void sendBroadcast(String title, String message) {

        // 1. Save the global notification first
        NotificationEntity notification = NotificationEntity.builder()
                .title(title)
                .message(message)
                .broadcast(true)
                .createdAt(Instant.now())
                .build();

        // Save to a new variable to satisfy the "effectively final" rule for the lambda
        final NotificationEntity savedNotification = notificationRepository.save(notification);

        // 2. Fetch ALL users
        // IMPORTANT: Verify this returns all users you expect to see the notification
        List<UserEntity> users = userRepository.findAll();
        System.out.println("Broadcasting to " + users.size() + " users.");

        // 3. Create a mapping for EVERY user
        List<UserNotificationEntity> mappings = users.stream()
                .map(user -> UserNotificationEntity.builder()
                        .user(user)
                        .notification(savedNotification) // Use the final saved entity
                        .read(false)
                        .build())
                .toList();

        // 4. Batch save all mappings
        userNotificationRepository.saveAll(mappings);
    }

    public List<NotificationResponse> myNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userNotificationRepository.findWithNotificationByUserId(user.getId())
                .stream()
                .map(un -> NotificationResponse.builder()
                        .id(un.getNotification().getId())
                        .title(un.getNotification().getTitle())
                        .message(un.getNotification().getMessage())
                        .read(un.isRead())
                        .createdAt(un.getNotification().getCreatedAt())
                        .build())
                .toList();
    }

    public long unreadCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userNotificationRepository.countByUserIdAndReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserNotificationEntity un = userNotificationRepository
                .findByUserIdAndNotificationId(user.getId(), notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!un.isRead()) {
            un.setRead(true);
            un.setReadAt(Instant.now());
        }
    }

    @Transactional
    public void markAllAsRead() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userNotificationRepository.findWithNotificationByUserId(user.getId())
                .stream()
                .filter(un -> !un.isRead())
                .forEach(un -> {
                    un.setRead(true);
                    un.setReadAt(Instant.now());
                });
    }
}
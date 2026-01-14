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
    public void sendBroadcast(String title, String message) {

        NotificationEntity notification = notificationRepository.save(
                NotificationEntity.builder()
                        .title(title)
                        .message(message)
                        .broadcast(true)
                        .build()
        );

        List<UserEntity> users = userRepository.findAll();

        List<UserNotificationEntity> mappings = users.stream()
                .map(user -> UserNotificationEntity.builder()
                        .user(user)
                        .notification(notification)
                        .read(false)
                        .build())
                .toList();

        userNotificationRepository.saveAll(mappings);
    }

    /**
     * Logged-in user's notifications
     */
    public List<NotificationResponse> myNotifications() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        return userNotificationRepository
                .findWithNotificationByUserId(user.getId())
                .stream()
                .map(un -> NotificationResponse.builder()
                        .id(un.getNotification().getId())
                        .title(un.getNotification().getTitle())
                        .message(un.getNotification().getMessage())
                        .read(un.isRead())
                        .createdAt(un.getNotification().getCreatedAt())
                        .build()
                )
                .toList();
    }


    /**
     * Unread count (topbar badge)
     */
    public long unreadCount() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        return userNotificationRepository
                .countByUserIdAndReadFalse(user.getId());
    }



    @Transactional
    public void markAsRead(Long notificationId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

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

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        userNotificationRepository
                .findWithNotificationByUserId(user.getId())
                .stream()
                .filter(un -> !un.isRead())
                .forEach(un -> {
                    un.setRead(true);
                    un.setReadAt(Instant.now());
                });
    }


}

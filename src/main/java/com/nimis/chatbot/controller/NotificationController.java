package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * ADMIN: Send notification to ALL users
     */
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(
            @RequestParam String title,
            @RequestParam String message) {

        notificationService.sendBroadcast(title, message);
        return ResponseEntity.ok("Notification sent to all users");
    }

    /**
     * USER: Fetch my notifications
     */
    @GetMapping("/me")
    public ResponseEntity<?> myNotifications() {
        return ResponseEntity.ok(notificationService.myNotifications());
    }


    /**
     * USER: Unread count (topbar bell)
     */
    @GetMapping("/me/unread-count")
    public ResponseEntity<?> unreadCount() {
        return ResponseEntity.ok(notificationService.unreadCount());
    }

    @PatchMapping("/me/read/{notificationId}")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

}

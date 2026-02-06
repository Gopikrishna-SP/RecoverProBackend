package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> myNotifications() {
        return ResponseEntity.ok(notificationService.myNotifications());
    }


    /**
     * USER: Unread count (topbar bell)
     */
    @GetMapping("/me/unread-count")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> unreadCount() {
        return ResponseEntity.ok(notificationService.unreadCount());
    }

    @PatchMapping("/me/read/{notificationId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/read-all")
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

}

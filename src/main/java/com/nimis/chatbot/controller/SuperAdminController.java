package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.SuperAdminDashboardResponse;
import com.nimis.chatbot.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor


public class SuperAdminController {
    private final SuperAdminService superAdminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<SuperAdminDashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(superAdminService.getDashboardStats());
    }
}
package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.*;
import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.service.AdminManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super")
@RequiredArgsConstructor
public class SuperAdminController {

    private final AdminManagementService adminService;

    @PostMapping("/banks")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BankResponse> createBank(@Valid @RequestBody CreateBankRequest req) {
        return ResponseEntity.ok(adminService.createBank(req));
    }

    @PostMapping("/bank-admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createBankAdmin(@Valid @RequestBody CreateBankAdminRequest req) {
        return ResponseEntity.ok(adminService.createBankAdmin(req));
    }
}

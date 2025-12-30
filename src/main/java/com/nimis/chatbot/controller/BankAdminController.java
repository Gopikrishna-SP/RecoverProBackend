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
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankAdminController {

    private final AdminManagementService adminService;

    @PostMapping("/vendor")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<VendorResponse> createCompany(@Valid @RequestBody CreateVendorRequest req) {
        return ResponseEntity.ok(adminService.createCompany(req));
    }

    @PostMapping("/vendor-admins")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<UserResponse> createVendorAdmin(@Valid @RequestBody CreateVendorAdminRequest req) {
        return ResponseEntity.ok(adminService.createVendorAdmin(req));
    }
}

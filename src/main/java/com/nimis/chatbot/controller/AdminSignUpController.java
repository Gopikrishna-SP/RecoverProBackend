package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.*;
import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.service.AdminSignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super")
@RequiredArgsConstructor
public class AdminSignUpController {

    private final AdminSignUpService adminService;

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

    @PostMapping("/vendor")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<VendorResponse> createCompany(@Valid @RequestBody CreateVendorRequest req) {
        return ResponseEntity.ok(adminService.createCompany(req));
    }

    @PostMapping("/vendor-admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createVendorAdmin(@Valid @RequestBody CreateVendorAdminRequest req) {
        return ResponseEntity.ok(adminService.createVendorAdmin(req));
    }

    @PostMapping("/fos")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponse> createFO(@Valid @RequestBody CreateFORequest req) {
        return ResponseEntity.ok(adminService.createFO(req));
    }
}

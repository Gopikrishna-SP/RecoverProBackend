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
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorAdminController {

    private final AdminManagementService adminService;

    @PostMapping("/fos")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<UserResponse> createFO(@Valid @RequestBody CreateFORequest req) {
        return ResponseEntity.ok(adminService.createFO(req));
    }
}

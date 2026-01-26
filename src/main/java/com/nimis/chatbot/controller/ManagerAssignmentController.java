package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.ManagerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerAssignmentController {

    private final ManagerAssignmentService managerAssignmentService;

//    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/assign")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public void assignCases(
            @RequestParam Long userId,
            @RequestBody List<Long> allocationIds
    ) {
        managerAssignmentService.assignToUser(userId, allocationIds);
    }
}

package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.FieldExecutiveCaseResponse;
import com.nimis.chatbot.dto.response.FieldExecutiveDashboardCaseResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.service.FieldExecutiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fe")
@RequiredArgsConstructor
public class FieldExecutiveController {

    private final FieldExecutiveService fieldExecutiveService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats(Authentication authentication) {
        System.out.println("=== Stats Called ===");
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            System.out.println("User: " + user.getId());

            Map<String, Long> stats = fieldExecutiveService.getDashboardStats(user.getId());
            System.out.println("Stats: " + stats);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/dashboard/cases")
    public List<FieldExecutiveDashboardCaseResponse> dashboardCases(
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return fieldExecutiveService.getDashboardCases(user.getId());
    }


    // FE -> View assigned cases
//    @PreAuthorize("hasRole('FO')")
    @GetMapping("/cases")
    public List<FieldExecutiveCaseResponse> myCases(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return fieldExecutiveService.getMyCases(user.getId());
    }

    @GetMapping("/cases/{loanNumber}/addresses")
    public List<String> getVisitAddresses(
            @PathVariable String loanNumber,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return fieldExecutiveService.getVisitAddresses(user.getId(), loanNumber);
    }




}

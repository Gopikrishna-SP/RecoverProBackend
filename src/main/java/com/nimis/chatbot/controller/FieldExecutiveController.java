package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.FieldExecutiveCaseResponse;
import com.nimis.chatbot.dto.response.FieldExecutiveDashboardCaseResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.service.FieldExecutiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fe")
@RequiredArgsConstructor
public class FieldExecutiveController {

    private final FieldExecutiveService fieldExecutiveService;

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

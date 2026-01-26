package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.UserProfileRequest;
import com.nimis.chatbot.dto.response.UserProfileResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UserProfileRequest request,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return ResponseEntity.ok(
                userProfileService.updateProfile(user.getId(), request)
        );
    }
}

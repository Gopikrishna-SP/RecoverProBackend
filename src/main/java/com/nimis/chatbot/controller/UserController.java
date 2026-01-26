package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.UserProfileResponse;
import com.nimis.chatbot.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping
    public List<UserProfileResponse> getAllFOnVendorUsers() {
        return userProfileService.getAllUsers();
    }
}
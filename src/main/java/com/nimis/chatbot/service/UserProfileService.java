package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.UserProfileResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    /**
     * Returns logged-in user's profile details
     * Used for Topbar Profile View
     */
    public UserProfileResponse getMyProfile() {

        // Email is stored as username in Spring Security
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .bankName(user.getBank() != null ? user.getBank().getName() : null)
                .vendorName(user.getVendor() != null ? user.getVendor().getName() : null)
                .roles(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toSet())
                )
                .enabled(user.isEnabled())
                .build();
    }
}

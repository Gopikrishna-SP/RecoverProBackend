package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.request.UserProfileRequest;
import com.nimis.chatbot.dto.response.UserProfileResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserRepository userRepository;

    /**
     * Returns logged-in user's profile details
     * Used for Topbar Profile View
     */
    public UserProfileResponse getMyProfile() {
        String email = getAuthenticatedUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    /**
     * Get user profile by ID
     */
    public UserProfileResponse getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    /**
     * Update logged-in user's profile
     */
    @Transactional
    public UserProfileResponse updateProfile(Long id, UserProfileRequest request) {
        String email = getAuthenticatedUserEmail();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getOrganization() != null) {
            user.setOrganization(request.getOrganization());
        }

        UserEntity updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    /**
     * Helper method to get authenticated user email
     */
    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_VENDOR_ADMIN") ||
                                role.getName().equals("ROLE_FO")))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map UserEntity to UserProfileResponse
     */
    private UserProfileResponse mapToResponse(UserEntity user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .location(user.getLocation())          // ✅ FIX
                .organization(user.getOrganization())  // ✅ FIX
                .bankName(user.getBank() != null ? user.getBank().getName() : null)
                .vendorName(user.getVendor() != null ? user.getVendor().getName() : null)
                .roles(user.getRoles() != null
                        ? user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet())
                        : Collections.emptySet())
                .enabled(user.isEnabled())
                .build();
    }

}
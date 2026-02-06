package com.nimis.chatbot.security;

import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService for Spring Security
 * ✅ Production-grade with caching and error handling
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email (username) with caching
     * ✅ Caching to reduce database hits
     * ✅ Comprehensive error handling
     */
    @Override
    @Cacheable(value = "users", key = "#email", unless = "#result == null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            if (email == null || email.isEmpty()) {
                log.warn("⚠️ Email parameter is null or empty");
                throw new UsernameNotFoundException("Email cannot be empty");
            }

            // Email is case-insensitive in some systems - trim and normalize
            String normalizedEmail = email.trim().toLowerCase();

            log.debug("🔍 Loading user by email: {}", normalizedEmail);

            // Query database
            UserEntity user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> {
                        log.warn("⚠️ User not found with email: {}", normalizedEmail);
                        return new UsernameNotFoundException("User not found with email: " + normalizedEmail);
                    });

            // Verify user is enabled
            if (!user.isEnabled()) {
                log.warn("⚠️ User account is disabled: {}", normalizedEmail);
                throw new UsernameNotFoundException("User account is disabled: " + normalizedEmail);
            }

            // Verify user has roles
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                log.warn("⚠️ User has no roles assigned: {}", normalizedEmail);
                throw new UsernameNotFoundException("User has no roles assigned: " + normalizedEmail);
            }

            log.info("✅ User loaded successfully: {} | Roles: {}",
                    normalizedEmail, user.getRoles().size());

            return user; // UserEntity implements UserDetails

        } catch (UsernameNotFoundException ex) {
            // Re-throw as-is
            throw ex;
        } catch (Exception ex) {
            log.error("❌ Unexpected error loading user by email: {}", ex.getMessage(), ex);
            throw new UsernameNotFoundException("Error loading user: " + ex.getMessage(), ex);
        }
    }

    /**
     * Load user by ID
     * ✅ Additional method for loading user by ID if needed
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        try {
            if (userId == null || userId <= 0) {
                log.warn("⚠️ Invalid user ID: {}", userId);
                throw new UsernameNotFoundException("Invalid user ID");
            }

            log.debug("🔍 Loading user by ID: {}", userId);

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("⚠️ User not found with ID: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });

            if (!user.isEnabled()) {
                log.warn("⚠️ User account is disabled: {}", userId);
                throw new UsernameNotFoundException("User account is disabled");
            }

            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                log.warn("⚠️ User has no roles assigned: {}", userId);
                throw new UsernameNotFoundException("User has no roles assigned");
            }

            log.info("✅ User loaded by ID: {} | Email: {}", userId, user.getEmail());

            return user;

        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("❌ Unexpected error loading user by ID {}: {}", userId, ex.getMessage(), ex);
            throw new UsernameNotFoundException("Error loading user: " + ex.getMessage(), ex);
        }
    }
}
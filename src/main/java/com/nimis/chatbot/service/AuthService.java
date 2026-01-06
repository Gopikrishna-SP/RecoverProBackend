package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserEntity getCurrentUserEntity() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        String username = auth.getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found in DB")
                );
    }
}

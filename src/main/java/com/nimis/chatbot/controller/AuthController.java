package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.LoginRequest;
import com.nimis.chatbot.dto.response.LoginResponse;
import com.nimis.chatbot.dto.response.MessageResponse;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = (UserEntity) authentication.getPrincipal();

            String token = jwtUtils.generateTokenFromUsername(user);

            List<String> roles = user.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .username(user.getFullName())
                    .email(user.getEmail())
                    .id(user.getId())
                    .roles(roles)
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            log.warn("Invalid login attempt for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password"));
        }
    }
}

package com.nimis.chatbot.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String username; // employee full name
    private String email;    // login email
    private List<String> roles;
}

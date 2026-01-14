package com.nimis.chatbot.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String mobile;

    private String bankName;
    private String vendorName;

    private Set<String> roles;
    private boolean enabled;
}

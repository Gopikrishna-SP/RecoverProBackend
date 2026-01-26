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
    private String firstName;
    private String lastName;
    private String phone;
    private String location;
    private String organization;

    private String bankName;
    private String vendorName;

    private Set<String> roles;
    private boolean enabled;
}

package com.nimis.chatbot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 15, message = "Phone must be 10-15 characters")
    private String phone;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be 2-100 characters")
    private String location;

    @NotBlank(message = "Organization is required")
    @Size(min = 2, max = 100, message = "Organization must be 2-100 characters")
    private String organization;

    @Email(message = "Email must be valid")
    private String email;

    private Long bankId;

    private Long vendorId;
}
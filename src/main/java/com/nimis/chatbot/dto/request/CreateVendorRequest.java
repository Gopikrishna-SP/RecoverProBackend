package com.nimis.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVendorRequest {
    @NotNull
    private Long bankId;

    @NotBlank
    private String name;
}
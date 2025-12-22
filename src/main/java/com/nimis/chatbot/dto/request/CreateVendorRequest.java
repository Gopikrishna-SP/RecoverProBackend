package com.nimis.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVendorRequest {
    @NotBlank
    private String name;

    // type should be VENDOR in typical bank-admin flow; include for future flexibility
    @NotNull
    private String type; // "VENDOR" - validated in service
}

package com.nimis.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBankRequest {
    @NotBlank
    private String bankName;
}

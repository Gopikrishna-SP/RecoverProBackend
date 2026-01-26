package com.nimis.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {
    private String message;
    private Integer otpExpirySeconds; // Frontend timer
}
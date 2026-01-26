package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorResponse {
    private Long id;
    private String name;
    private Long bankId;
}
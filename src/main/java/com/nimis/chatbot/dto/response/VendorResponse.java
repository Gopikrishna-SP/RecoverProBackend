package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponse {
    private Long id;
    private Long bankId;
    private String name;

}

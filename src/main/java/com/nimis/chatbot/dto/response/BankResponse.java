package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankResponse {
    private Long id;
    private String name;
}

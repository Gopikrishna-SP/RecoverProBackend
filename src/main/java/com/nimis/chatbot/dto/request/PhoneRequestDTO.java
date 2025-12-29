package com.nimis.chatbot.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequestDTO {

    private Integer priority; // 1–10
    private String phoneNumber;
}

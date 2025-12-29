package com.nimis.chatbot.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    private Integer priority; // 1–10
    private String addressLine;

    private String businessPinCode;
    private String residencePinCode;
    private String mainPinCode;
}

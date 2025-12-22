package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponseDTO {
    private Long id;
    private Long bankId;
    private String name;

}

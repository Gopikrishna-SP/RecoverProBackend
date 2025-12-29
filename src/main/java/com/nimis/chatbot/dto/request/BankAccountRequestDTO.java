package com.nimis.chatbot.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequestDTO {

    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
}

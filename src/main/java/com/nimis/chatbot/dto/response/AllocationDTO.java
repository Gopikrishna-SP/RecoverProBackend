package com.nimis.chatbot.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocationDTO {
    private Long id;
    private String caseId;
    private String loanNumber;
    private String borrowerName;
    private String customerName;
    private String location;
    private String phone;
    private String address;
    private String loanAmount;
    private String amount;
    private String status;
    private Long fieldExecutiveId;
    private String date;
}
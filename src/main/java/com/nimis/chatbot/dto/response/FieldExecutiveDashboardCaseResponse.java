package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldExecutiveDashboardCaseResponse {
    private String caseId;
    private String loanNumber;
    private String borrowerName;
    private String loanAmount;
    private String status;
    private String phone;
    private String location;
    private String address;
}

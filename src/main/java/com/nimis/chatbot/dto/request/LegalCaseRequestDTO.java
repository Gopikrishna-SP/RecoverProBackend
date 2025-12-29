package com.nimis.chatbot.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegalCaseRequestDTO {

    private String caseType; // ARBITRATION / SEC_138 / SEC_420

    private String courtForum;
    private String caseNumber;
    private String locationFiling;

    private String processStage;
    private LocalDate dateOfFiling;
    private LocalDate listingDate;
    private LocalDate lastDateOfHearing;
    private LocalDate nextDateOfHearing;

    private BigDecimal claimAmount;

    private String advocateName;
    private String advocateContactNumber;
    private String authorizedOfficer;

    private String remarks;
}

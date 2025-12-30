package com.nimis.chatbot.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LegalCaseResponse {

    private String caseType;
    private String processStage;
    private String revisedStage;

    private LocalDate noticeDate;
    private LocalDate filingDate;
    private LocalDate listingDate;

    private String courtForum;
    private String courtLocation;
    private String caseNumber;

    private BigDecimal claimAmount;

    private String advocateName;
    private String advocateContact;

    private String remarks;
}

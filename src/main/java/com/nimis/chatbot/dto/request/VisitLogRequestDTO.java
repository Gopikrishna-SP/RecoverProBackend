package com.nimis.chatbot.dto.request;

import com.nimis.chatbot.model.enums.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLogRequestDTO {

    // Loan Info
    private Long allocationId;
    private String loanNumber;
    private String segment;
    private String product;
    private String state;
    private String branch;
    private String location;
    private String customerName;
    private BigDecimal posInCr;
    private BigDecimal emi;
    private String bkt;

    // Visit Assessment (Required)
    private Disp disp;
    private Contactability contactability;
    private ResidenceStatus residenceStatus;
    private ClassificationCode classificationCode;

    // Visit Assessment (Optional)
    private OfficeStatus officeStatus;
    private ReasonForDefault reasonForDefault;
    private String projection;
    private String customerProfile;

    // Visit Details
    private BigDecimal amount;
    private String ptpDate;
    private String fieldUpdateFeedback;
    private String visitDate;
}

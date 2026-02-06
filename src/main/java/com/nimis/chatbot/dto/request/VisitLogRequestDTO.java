package com.nimis.chatbot.dto.request;

import com.nimis.chatbot.model.enums.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitLogRequestDTO {

    // Loan & Allocation
    private String loanNumber;
    private Long allocationId;
    private Long visitAddressId;  // NEW: Address ID from addresses endpoint

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
    private String visitDate;
    private BigDecimal amount;
    private String ptpDate;
    private String fieldUpdateFeedback;

    // GPS Location Data (NEW)
    private Double latitude;
    private Double longitude;
    private Double gpsAccuracy;
    private Double gpsAltitude;
}
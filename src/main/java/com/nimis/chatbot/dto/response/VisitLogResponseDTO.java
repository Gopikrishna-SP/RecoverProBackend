package com.nimis.chatbot.dto.response;

import com.nimis.chatbot.model.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitLogResponseDTO {

    // Response ID
    private Long id;

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

    // Visit Assessment
    private Disp disp;
    private Contactability contactability;
    private ResidenceStatus residenceStatus;
    private ClassificationCode classificationCode;
    private OfficeStatus officeStatus;
    private ReasonForDefault reasonForDefault;
    private String projection;
    private String customerProfile;

    // Visit Details
    private LocalDate visitDate;
    private BigDecimal amount;
    private LocalDate ptpDate;
    private String fieldUpdateFeedback;
    private String visitImagePath;

    // GPS Location Data (NEW)
    private Double latitude;
    private Double longitude;
    private Double gpsAccuracy;
    private Double gpsAltitude;
    private LocalDateTime gpsCapturedAt;
    private String gpsAddress;
    private Double distanceFromExpectedLocation;

    // Collection & Status Info
    private String collectionStatus;
    private String visitStatus;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime depositedAt;
    private LocalDateTime submittedAt;
    private String rejectionReason;

    private Long userId;
    private String createdBy;
    private LocalDate createdDate;
}
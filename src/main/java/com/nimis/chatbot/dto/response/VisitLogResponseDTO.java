package com.nimis.chatbot.dto.response;

import com.nimis.chatbot.model.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitLogResponseDTO {

    private String segment;
    private String product;
    private String state;
    private String branch;
    private String location;
    private String loanNumber;
    private String customerName;
    private BigDecimal posInCr;
    private BigDecimal emi;
    private String bkt;

    private Long visitId;
    private LocalDate visitDate;
    private Disp disp;
    private String projection;
    private BigDecimal amount;
    private LocalDate ptpDate;

    private ReasonForDefault reasonForDefault;
    private Contactability contactability;
    private ResidenceStatus residenceStatus;
    private OfficeStatus officeStatus;
    private ClassificationCode classificationCode;

    private String fieldUpdateFeedback;
    private String visitImagePath;
    private Double latitude;
    private Double longitude;
    private String geoAddress;
    private String createdBy;
}

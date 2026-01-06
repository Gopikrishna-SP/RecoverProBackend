package com.nimis.chatbot.dto.request;

import com.nimis.chatbot.model.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLogRequestDTO {

    private Long allocationId;

    private Disp disp;
    private String projection;
    private BigDecimal amount;
    private LocalDate ptpDate;

    private ReasonForDefault reasonForDefault;
    private Contactability contactability;
    private ResidenceStatus residenceStatus;
    private OfficeStatus officeStatus;
    private ClassificationCode classificationCode;

    private String customerProfile;
    private String fieldUpdateFeedback;

    private LocalDate visitDate;
}

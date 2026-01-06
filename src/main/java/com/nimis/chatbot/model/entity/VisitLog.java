package com.nimis.chatbot.model.entity;

import com.nimis.chatbot.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_id", nullable = false)
    private Long allocationId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Enumerated(EnumType.STRING)
    private Disp disp;

    private String projection;
    private BigDecimal amount;
    private LocalDate ptpDate;

    @Enumerated(EnumType.STRING)
    private ReasonForDefault reasonForDefault;

    @Enumerated(EnumType.STRING)
    private Contactability contactability;

    @Enumerated(EnumType.STRING)
    private ResidenceStatus residenceStatus;

    @Enumerated(EnumType.STRING)
    private OfficeStatus officeStatus;

    @Enumerated(EnumType.STRING)
    private ClassificationCode classificationCode;

    @Column(columnDefinition = "TEXT")
    private String customerProfile;

    @Column(columnDefinition = "TEXT")
    private String fieldUpdateFeedback;

    private Double latitude;
    private Double longitude;
    private Double locationAccuracy;
    private String geoAddress;

    private String visitImagePath;
}

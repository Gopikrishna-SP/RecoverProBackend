package com.nimis.chatbot.model.entity;

import com.nimis.chatbot.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "visit_log", indexes = {
        @Index(name = "idx_allocation_id", columnList = "allocation_id"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_visit_date", columnList = "visit_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    // Loan Info (Now persisted)
    @Column(name = "loan_number")
    private String loanNumber;

    @Column(name = "segment")
    private String segment;

    @Column(name = "product")
    private String product;

    @Column(name = "state")
    private String state;

    @Column(name = "branch")
    private String branch;

    @Column(name = "location")
    private String location;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "pos_in_cr")
    private BigDecimal posInCr;

    @Column(name = "emi")
    private BigDecimal emi;

    @Column(name = "bkt")
    private String bkt;

    // Visit Assessment (Required)
    @Enumerated(EnumType.STRING)
    private Disp disp;

    @Enumerated(EnumType.STRING)
    private Contactability contactability;

    @Enumerated(EnumType.STRING)
    private ResidenceStatus residenceStatus;

    @Enumerated(EnumType.STRING)
    private ClassificationCode classificationCode;

    // Visit Assessment (Optional)
    @Enumerated(EnumType.STRING)
    private OfficeStatus officeStatus;

    @Enumerated(EnumType.STRING)
    private ReasonForDefault reasonForDefault;

    @Column(name = "projection")
    private String projection;

    @Column(name = "customer_profile")
    private String customerProfile;

    // Visit Details
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "ptp_date")
    private LocalDate ptpDate;

    @Column(name = "field_update_feedback", columnDefinition = "TEXT")
    private String fieldUpdateFeedback;

    @Column(name = "visit_image_path")
    private String visitImagePath;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
    }
}
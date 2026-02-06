package com.nimis.chatbot.model.entity;

import com.nimis.chatbot.model.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_log", indexes = {
        @Index(name = "idx_allocation_id", columnList = "allocation_id"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_visit_date", columnList = "visit_date"),
        @Index(name = "idx_collection_status", columnList = "collection_status"),
        @Index(name = "idx_latitude_longitude", columnList = "latitude, longitude")
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

    // GPS Location Fields (Production Grade)
    @Column(name = "latitude")
    private Double latitude;  // GPS latitude coordinate

    @Column(name = "longitude")
    private Double longitude;  // GPS longitude coordinate

    @Column(name = "gps_accuracy")
    private Double gpsAccuracy;  // Accuracy in meters

    @Column(name = "gps_altitude")
    private Double gpsAltitude;  // Altitude above sea level

    @Column(name = "gps_captured_at")
    private LocalDateTime gpsCapturedAt;  // When GPS was captured

    @Column(name = "gps_address", columnDefinition = "TEXT")
    private String gpsAddress;  // Reverse geocoded address (optional)

    @Column(name = "distance_from_expected_location")
    private Double distanceFromExpectedLocation;  // Distance in km from expected address

    // Collection Approval Fields
    /**
     * Collection Status Flow:
     * PENDING_APPROVAL (initial) -> APPROVED (by bank admin) -> DEPOSITED (final)
     *                            -> REJECTED (with reason)
     */
    @Column(name = "collection_status", length = 50)
    private String collectionStatus;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;  // User ID or name of who approved/rejected

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;  // When approved or rejected

    @Column(name = "deposited_at")
    private LocalDateTime depositedAt;  // When actually deposited to bank

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;  // Why rejected

    @Column(name = "visit_status", length = 50)
    private String visitStatus;  // STARTED, IN_PROGRESS, COMPLETED, SUBMITTED

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;  // When visit was submitted

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDate.now();
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
}
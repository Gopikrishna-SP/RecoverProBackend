package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "legal_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    // Case info
    private String caseType;       // SEC17 / SEC25 / SEC138 / SEC420
    private String processStage;
    private String revisedStage;

    private String monthLastNotice;
    private String monthOfNotice;
    private LocalDate noticeDate;
    private LocalDate filingConfirmationDate;

    private String lrn1;
    private String lrnMonth2;
    private String lrn2;
    private String lrnMonth3;
    private String lrn3;

    private LocalDate arbitrationInvocationDate1;
    private LocalDate arbitrationInvocationDate2;
    private LocalDate tentativeReferenceDate;
    private LocalDate letterToArbitratorDate;
    private LocalDate tentativeFreezeDate;

    private LocalDate sec17OrderDate;
    private String relief;

    // SEC25 fields
    private String sec25Filed;
    private String sec25ProcessStage;
    private LocalDate sec25LDOH;
    private LocalDate sec25NDOH;

    // SEC138 fields
    private String sec138Filed;
    private String sec138ProcessStage;
    private LocalDate sec138LDOH;
    private LocalDate sec138NDOH;

    // SEC420 fields
    private String sec420ProcessStage;
    private LocalDate sec420LDOH;
    private LocalDate sec420NDOH;
    private LocalDate sec420ListingDate;
    private LocalDate caseWithdrawalDate;

    // Court info
    private LocalDate listingDate;
    private LocalDate filingDate;
    private String courtForum;
    private String courtLocation;
    private String caseNumber;
    private BigDecimal claimAmount;

    private String advocateName;
    private String advocateContact;
    private String authorizedOfficer;

    // Stage 1
    private String stage1Verification;
    private LocalDate stage1HearingOn;

    // Stage 2
    private String stage2SummonsStage;
    private String stage2SummonsStatus;

    // Stage 3
    private String stage3Appearance;
    private LocalDate stage3HearingOn;

    // Stage 4
    private LocalDate stage4BailableWarrantDate;
    private String stage4BailableWarrantStatus;

    // Stage 5
    private LocalDate stage5NonBailableWarrantDate;
    private LocalDate bwReissuedDate;
    private LocalDate bwWarrantPostDate;
    private LocalDate bwReissuedCollectionDate;
    private String stage5NonBailableWarrantStatus;
    private String stage5NonBailableWarrantCollectedStatus;
    private LocalDate stage5ProclamationIssuedDate;
    private String stage5ProclamationStatus;
    private String stage5ProclamationCollectedStatus;
    private LocalDate stage5AttachmentIssuedDate;
    private String stage5AttachmentStatus;
    private String stage5AttachmentCollectedStatus;

    // Stage 6
    private LocalDate stage6NonBailableReissueDate;
    private String stage6NonBailableReissueStatus;
    private LocalDate nonBailableWarrantReissuedDate;
    private LocalDate nonBailableWarrantReissueCollectionDate;

    // Stage 7
    private LocalDate stage7NonBailableReissueDate;
    private String stage7NonBailableReissueStatus;

    // Stage 8
    private LocalDate stage8NonBailableReissueDate;
    private String stage8NonBailableReissueStatus;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}

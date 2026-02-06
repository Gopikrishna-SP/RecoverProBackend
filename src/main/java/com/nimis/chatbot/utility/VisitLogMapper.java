package com.nimis.chatbot.utility;

import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;
import java.math.BigDecimal;
import java.util.Map;

public class VisitLogMapper {

    // Single param - MVP (direct mapping from VisitLog transient fields)
    public static VisitLogResponseDTO toResponse(VisitLog visitLog) {
        return VisitLogResponseDTO.builder()
                .id(visitLog.getId())

                // Loan Info (from transient fields or allocation)
                .allocationId(visitLog.getAllocationId())
                .loanNumber(visitLog.getLoanNumber())
                .segment(visitLog.getSegment())
                .product(visitLog.getProduct())
                .state(visitLog.getState())
                .branch(visitLog.getBranch())
                .location(visitLog.getLocation())
                .customerName(visitLog.getCustomerName())
                .posInCr(visitLog.getPosInCr())
                .emi(visitLog.getEmi())
                .bkt(visitLog.getBkt())

                // Visit Assessment
                .disp(visitLog.getDisp())
                .contactability(visitLog.getContactability())
                .residenceStatus(visitLog.getResidenceStatus())
                .classificationCode(visitLog.getClassificationCode())
                .officeStatus(visitLog.getOfficeStatus())
                .reasonForDefault(visitLog.getReasonForDefault())
                .projection(visitLog.getProjection())
                .customerProfile(visitLog.getCustomerProfile())

                // Visit Details
                .visitDate(visitLog.getVisitDate())
                .amount(visitLog.getAmount())
                .ptpDate(visitLog.getPtpDate())
                .fieldUpdateFeedback(visitLog.getFieldUpdateFeedback())
                .visitImagePath(visitLog.getVisitImagePath())

                // GPS Location Data (NEW)
                .latitude(visitLog.getLatitude())
                .longitude(visitLog.getLongitude())
                .gpsAccuracy(visitLog.getGpsAccuracy())
                .gpsAltitude(visitLog.getGpsAltitude())
                .gpsCapturedAt(visitLog.getGpsCapturedAt())
                .gpsAddress(visitLog.getGpsAddress())
                .distanceFromExpectedLocation(visitLog.getDistanceFromExpectedLocation())

                // Collection & Status Info (NEW)
                .collectionStatus(visitLog.getCollectionStatus())
                .visitStatus(visitLog.getVisitStatus())
                .approvedBy(visitLog.getApprovedBy())
                .approvedAt(visitLog.getApprovedAt())
                .depositedAt(visitLog.getDepositedAt())
                .submittedAt(visitLog.getSubmittedAt())
                .rejectionReason(visitLog.getRejectionReason())

                // Metadata
                .userId(visitLog.getUserId())
                .createdBy(visitLog.getCreatedBy())
                .createdDate(visitLog.getCreatedDate())
                .build();
    }

    // Two params - fetch from allocation JSON (for future use)
    public static VisitLogResponseDTO toResponse(
            VisitLog visitLog,
            Allocation allocation
    ) {
        Map<String, Object> data = allocation.getAllocationData();

        return VisitLogResponseDTO.builder()
                .id(visitLog.getId())

                // Loan Info from Allocation JSON
                .allocationId(allocation.getId())
                .loanNumber(allocation.getLoanNumber())
                .segment((String) data.get("SEGMENT"))
                .product((String) data.get("PRODUCT"))
                .state((String) data.get("STATE"))
                .branch((String) data.get("BRANCH"))
                .location((String) data.get("LOCATION"))
                .customerName((String) data.get("CUSTOMER NAME"))
                .posInCr(data.get("POS (IN CR)") != null
                        ? new BigDecimal(data.get("POS (IN CR)").toString())
                        : null)
                .emi(data.get("EMI") != null
                        ? new BigDecimal(data.get("EMI").toString())
                        : null)
                .bkt((String) data.get("OPENING BKT"))

                // Visit Assessment
                .disp(visitLog.getDisp())
                .contactability(visitLog.getContactability())
                .residenceStatus(visitLog.getResidenceStatus())
                .classificationCode(visitLog.getClassificationCode())
                .officeStatus(visitLog.getOfficeStatus())
                .reasonForDefault(visitLog.getReasonForDefault())
                .projection(visitLog.getProjection())
                .customerProfile(visitLog.getCustomerProfile())

                // Visit Details
                .visitDate(visitLog.getVisitDate())
                .amount(visitLog.getAmount())
                .ptpDate(visitLog.getPtpDate())
                .fieldUpdateFeedback(visitLog.getFieldUpdateFeedback())
                .visitImagePath(visitLog.getVisitImagePath())

                // GPS Location Data (NEW)
                .latitude(visitLog.getLatitude())
                .longitude(visitLog.getLongitude())
                .gpsAccuracy(visitLog.getGpsAccuracy())
                .gpsAltitude(visitLog.getGpsAltitude())
                .gpsCapturedAt(visitLog.getGpsCapturedAt())
                .gpsAddress(visitLog.getGpsAddress())
                .distanceFromExpectedLocation(visitLog.getDistanceFromExpectedLocation())

                // Collection & Status Info (NEW)
                .collectionStatus(visitLog.getCollectionStatus())
                .visitStatus(visitLog.getVisitStatus())
                .approvedBy(visitLog.getApprovedBy())
                .approvedAt(visitLog.getApprovedAt())
                .depositedAt(visitLog.getDepositedAt())
                .submittedAt(visitLog.getSubmittedAt())
                .rejectionReason(visitLog.getRejectionReason())

                // Metadata
                .userId(visitLog.getUserId())
                .createdBy(visitLog.getCreatedBy())
                .createdDate(visitLog.getCreatedDate())
                .build();
    }
}
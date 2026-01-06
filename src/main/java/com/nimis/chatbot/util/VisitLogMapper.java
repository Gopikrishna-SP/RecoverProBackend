package com.nimis.chatbot.util;

import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;

import java.math.BigDecimal;
import java.util.Map;

public class VisitLogMapper {

    public static VisitLogResponseDTO toResponse(
            VisitLog visitLog,
            Allocation allocation
    ) {

        Map<String, Object> data = allocation.getAllocationData();

        return VisitLogResponseDTO.builder()
                // Allocation JSON
                .segment((String) data.get("SEGMENT"))
                .product((String) data.get("PRODUCT"))
                .state((String) data.get("STATE"))
                .branch((String) data.get("BRANCH"))
                .location((String) data.get("LOCATION"))
                .loanNumber(allocation.getLoanNumber())
                .customerName((String) data.get("CUSTOMER NAME"))

                .posInCr(data.get("POS (IN CR)") != null
                        ? new BigDecimal(data.get("POS (IN CR)").toString())
                        : null)

                .emi(data.get("EMI") != null
                        ? new BigDecimal(data.get("EMI").toString())
                        : null)

                .bkt((String) data.get("OPENING BKT"))



                // Visit data
                .visitId(visitLog.getId())
                .visitDate(visitLog.getVisitDate())
                .disp(visitLog.getDisp())
                .projection(visitLog.getProjection())
                .amount(visitLog.getAmount())
                .ptpDate(visitLog.getPtpDate())
                .reasonForDefault(visitLog.getReasonForDefault())
                .contactability(visitLog.getContactability())
                .residenceStatus(visitLog.getResidenceStatus())
                .officeStatus(visitLog.getOfficeStatus())
                .classificationCode(visitLog.getClassificationCode())
                .fieldUpdateFeedback(visitLog.getFieldUpdateFeedback())
                .visitImagePath(visitLog.getVisitImagePath())
                .latitude(visitLog.getLatitude())
                .longitude(visitLog.getLongitude())
                .geoAddress(visitLog.getGeoAddress())
                .createdBy(visitLog.getCreatedBy())
                .build();
    }
}

package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.request.VisitLogRequestDTO;
import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.model.enums.*;
import com.nimis.chatbot.service.VisitLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/visit-logs")
@RequiredArgsConstructor
public class VisitLogController {

    private final VisitLogService visitLogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<VisitLogResponseDTO> createVisitLog(
            @RequestParam("loanNumber") String loanNumber,
            @RequestParam("disp") String disp,
            @RequestParam("contactability") String contactability,
            @RequestParam("residenceStatus") String residenceStatus,
            @RequestParam("classificationCode") String classificationCode,
            @RequestParam("visitDate") String visitDate,

            @RequestParam(value = "allocationId", required = false) Long allocationId,
            @RequestParam(value = "reasonForDefault", required = false) String reasonForDefault,
            @RequestParam(value = "officeStatus", required = false) String officeStatus,
            @RequestParam(value = "projection", required = false) String projection,
            @RequestParam(value = "customerProfile", required = false) String customerProfile,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "ptpDate", required = false) String ptpDate,
            @RequestParam(value = "fieldUpdateFeedback", required = false) String fieldUpdateFeedback,
            @RequestParam(value = "image", required = false) MultipartFile image,

            @RequestHeader(value = "X-Visit-Source", required = false) String visitSource,
            Principal principal
    ) throws Exception {

        log.info("CreateVisitLog request - loanNumber: {}, disp: {}, allocationId: {}",
                loanNumber, disp, allocationId);

        VisitLogRequestDTO request = VisitLogRequestDTO.builder()
                .loanNumber(loanNumber)
                .allocationId(allocationId)
                .disp(parseEnum(disp, Disp.class))
                .contactability(parseEnum(contactability, Contactability.class))
                .residenceStatus(parseEnum(residenceStatus, ResidenceStatus.class))
                .classificationCode(parseEnum(classificationCode, ClassificationCode.class))
                .visitDate(visitDate)
                .reasonForDefault(parseEnum(reasonForDefault, ReasonForDefault.class))
                .officeStatus(parseEnum(officeStatus, OfficeStatus.class))
                .projection(projection)
                .customerProfile(customerProfile)
                .amount(amount)
                .ptpDate(ptpDate)
                .fieldUpdateFeedback(fieldUpdateFeedback)
                .build();

        Authentication authentication = (Authentication) principal;
        UserEntity user = (UserEntity) authentication.getPrincipal();

// Debug logs
        log.info("User object: {}", user);
        log.info("User ID: {}", user.getId());
        log.info("User Full Name: {}", user.getFullName());
        log.info("User class: {}", user.getClass().getName());

// Check if getId() exists and returns value
        if (user.getId() == null) {
            log.error("WARNING: user.getId() is NULL!");
        }

        log.info("User: {} (ID: {}) creating visit log", user.getFullName(), user.getId());

        return ResponseEntity.ok(
                visitLogService.create(
                        request,
                        image,
                        user.getFullName(),
                        user.getId()
                )
        );
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumClass) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid enum value for {}: {}", enumClass.getSimpleName(), value);
            throw new IllegalArgumentException(
                    "Invalid value for " + enumClass.getSimpleName() + ": " + value
            );
        }
    }

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<List<VisitLogResponseDTO>> getByAllocation(
            @PathVariable Long allocationId
    ) {
        log.info("Fetching visit logs for allocationId: {}", allocationId);
        return ResponseEntity.ok(visitLogService.getByAllocationId(allocationId));
    }

    @GetMapping("/my-visits")
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<List<VisitLogResponseDTO>> getMyVisits(Principal principal) {
        Authentication authentication = (Authentication) principal;
        UserEntity user = (UserEntity) authentication.getPrincipal();

        Long userId = user.getId();
        log.info("Fetching visits for userId: {}", userId);

        return ResponseEntity.ok(visitLogService.getByUserId(userId));
    }

    @GetMapping("/allocation/get-all")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<List<VisitLogResponseDTO>> getAllVisitLogs() {
        log.info("Fetching all visit logs");
        return ResponseEntity.ok(visitLogService.getAll());
    }
}
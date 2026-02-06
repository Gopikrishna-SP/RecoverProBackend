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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/visit-logs")
@RequiredArgsConstructor
public class VisitLogController {

    private final VisitLogService visitLogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> createVisitLog(
            @RequestParam("loanNumber") String loanNumber,
            @RequestParam("disp") String disp,
            @RequestParam("contactability") String contactability,
            @RequestParam("residenceStatus") String residenceStatus,
            @RequestParam("classificationCode") String classificationCode,
            @RequestParam("visitDate") String visitDate,

            @RequestParam(value = "allocationId", required = false) String allocationIdParam,
            @RequestParam(value = "visitAddressId", required = false) String visitAddressIdParam,
            @RequestParam(value = "reasonForDefault", required = false) String reasonForDefault,
            @RequestParam(value = "officeStatus", required = false) String officeStatus,
            @RequestParam(value = "projection", required = false) String projection,
            @RequestParam(value = "customerProfile", required = false) String customerProfile,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "ptpDate", required = false) String ptpDate,
            @RequestParam(value = "fieldUpdateFeedback", required = false) String fieldUpdateFeedback,
            @RequestParam(value = "image", required = false) MultipartFile image,

            // GPS Parameters
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "gpsAccuracy", required = false) Double gpsAccuracy,
            @RequestParam(value = "gpsAltitude", required = false) Double gpsAltitude,

            @RequestHeader(value = "X-Visit-Source", required = false) String visitSource,
            Principal principal
    ) {
        try {
            // ✅ SECURE: Don't log actual GPS coordinates (privacy)
            log.info("CreateVisitLog request - loanNumber: {}, disp: {}, GPS: {}",
                    loanNumber, disp, latitude != null && longitude != null ? "CAPTURED" : "NOT_PROVIDED");

            // Parse allocationId safely
            Long allocationId = null;
            if (allocationIdParam != null && !allocationIdParam.isBlank() && !allocationIdParam.equals("undefined")) {
                try {
                    allocationId = Long.parseLong(allocationIdParam);
                } catch (NumberFormatException e) {
                    log.warn("Invalid allocationId format: {}", allocationIdParam);
                }
            }

            // Parse visitAddressId safely
            Long visitAddressId = null;
            if (visitAddressIdParam != null && !visitAddressIdParam.isBlank() && !visitAddressIdParam.equals("undefined")) {
                try {
                    visitAddressId = Long.parseLong(visitAddressIdParam);
                } catch (NumberFormatException e) {
                    log.warn("Invalid visitAddressId format: {}", visitAddressIdParam);
                }
            }

            VisitLogRequestDTO request = VisitLogRequestDTO.builder()
                    .loanNumber(loanNumber)
                    .allocationId(allocationId)
                    .visitAddressId(visitAddressId)
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
                    .latitude(latitude)
                    .longitude(longitude)
                    .gpsAccuracy(gpsAccuracy)
                    .gpsAltitude(gpsAltitude)
                    .build();

            Authentication authentication = (Authentication) principal;
            UserEntity user = (UserEntity) authentication.getPrincipal();

            log.info("User: {} (ID: {}) creating visit log", user.getFullName(), user.getId());

            VisitLogResponseDTO response = visitLogService.create(
                    request,
                    image,
                    user.getFullName(),
                    user.getId()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // ✅ SECURE: Sanitized error message
            log.warn("Validation error in visit log creation: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid input data", "message", "Please check your input"));

        } catch (Exception e) {
            // ✅ SECURE: Generic error message
            log.error("Error creating visit log: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to create visit log", "message", "Please try again"));
        }
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
                    "Invalid value for " + enumClass.getSimpleName()
            );
        }
    }

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> getByAllocation(@PathVariable Long allocationId) {
        try {
            log.info("Fetching visit logs for allocationId: {}", allocationId);
            List<VisitLogResponseDTO> logs = visitLogService.getByAllocationId(allocationId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching visit logs for allocation {}: {}", allocationId, e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch visit logs"));
        }
    }

    @GetMapping("/my-visits")
    @PreAuthorize("hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> getMyVisits(Principal principal) {
        try {
            Authentication authentication = (Authentication) principal;
            UserEntity user = (UserEntity) authentication.getPrincipal();

            Long userId = user.getId();
            log.info("Fetching visits for userId: {}", userId);

            List<VisitLogResponseDTO> visits = visitLogService.getByUserId(userId);
            return ResponseEntity.ok(visits);

        } catch (Exception e) {
            log.error("Error fetching user visits: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch visits"));
        }
    }

    @GetMapping("/allocation/get-all")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getAllVisitLogs() {
        try {
            log.info("Fetching all visit logs");
            List<VisitLogResponseDTO> logs = visitLogService.getAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching all visit logs: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch visit logs"));
        }
    }
}
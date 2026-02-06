package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.request.VisitLogRequestDTO;
import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;
import com.nimis.chatbot.repository.VisitLogRepository;
import com.nimis.chatbot.utility.VisitLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;
    private final AllocationUploadService allocationService;

    public VisitLogResponseDTO create(
            VisitLogRequestDTO request,
            MultipartFile image,
            String createdBy,
            Long userId
    ) {

        LocalDate visitDate = parseVisitDate(request.getVisitDate());

        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            imagePath = saveVisitImage(image, request.getAllocationId());
        }

        // Fetch allocation data by loanNumber
        String segment = null;
        String product = null;
        String state = null;
        String branch = null;
        String location = null;
        String customerName = null;
        BigDecimal posInCr = null;
        BigDecimal emi = null;
        String bkt = null;
        String loanNumber = request.getLoanNumber();
        Long resolvedAllocationId = request.getAllocationId();

        if (request.getLoanNumber() != null && !request.getLoanNumber().isBlank()) {
            try {
                log.info("Fetching allocation for loanNumber: {}", request.getLoanNumber());

                Allocation allocation = allocationService.getByLoanNumber(request.getLoanNumber());

                if (allocation == null) {
                    log.error("Allocation is NULL for loanNumber: {}", request.getLoanNumber());
                } else {
                    log.info("Allocation found, ID: {}", allocation.getId());

                    Map<String, Object> data = allocation.getAllocationData();

                    if (data == null) {
                        log.error("AllocationData is NULL");
                    } else {
                        log.info("AllocationData keys: {}", data.keySet());

                        resolvedAllocationId = allocation.getId();
                        loanNumber = allocation.getLoanNumber();

                        // Case-insensitive key matching
                        segment = getValueByKeyIgnoreCase(data, "SEGMENT");
                        product = getValueByKeyIgnoreCase(data, "PRODUCT");
                        state = getValueByKeyIgnoreCase(data, "STATE");
                        branch = getValueByKeyIgnoreCase(data, "BRANCH");
                        location = getValueByKeyIgnoreCase(data, "LOCATION");
                        customerName = getValueByKeyIgnoreCase(data, "CUSTOMER NAME");
                        bkt = getValueByKeyIgnoreCase(data, "OPENING BKT");

                        log.info("Extracted - segment: {}, product: {}, state: {}, branch: {}", segment, product, state, branch);
                        log.info("Extracted - location: {}, customerName: {}, bkt: {}", location, customerName, bkt);

                        posInCr = getBigDecimalByKeyIgnoreCase(data, "POS", "POS (IN CR)", "POS_IN_CR");
                        emi = getBigDecimalByKeyIgnoreCase(data, "EMI");

                        log.info("Extracted - posInCr: {}, emi: {}", posInCr, emi);
                    }
                }
            } catch (Exception e) {
                log.error("ERROR: Could not fetch allocation for loan {}: ", request.getLoanNumber(), e);
                e.printStackTrace();
            }
        }

        log.info("Creating VisitLog with - allocationId: {}, loanNumber: {}, segment: {}",
                resolvedAllocationId, loanNumber, segment);

        VisitLog visitLog = VisitLog.builder()
                .allocationId(resolvedAllocationId)
                .userId(userId)
                .createdBy(createdBy)
                .loanNumber(loanNumber)
                .segment(segment)
                .product(product)
                .state(state)
                .branch(branch)
                .location(location)
                .customerName(customerName)
                .posInCr(posInCr)
                .emi(emi)
                .bkt(bkt)
                .visitDate(visitDate)
                .disp(request.getDisp())
                .projection(request.getProjection())
                .amount(request.getAmount())
                .ptpDate(parsePtpDate(request.getPtpDate()))
                .reasonForDefault(request.getReasonForDefault())
                .contactability(request.getContactability())
                .residenceStatus(request.getResidenceStatus())
                .officeStatus(request.getOfficeStatus())
                .classificationCode(request.getClassificationCode())
                .customerProfile(request.getCustomerProfile())
                .fieldUpdateFeedback(request.getFieldUpdateFeedback())
                .visitImagePath(imagePath)
                // GPS Data
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .gpsAccuracy(request.getGpsAccuracy())
                .gpsAltitude(request.getGpsAltitude())
                .gpsCapturedAt(request.getLatitude() != null ? LocalDateTime.now() : null)
                .visitStatus("SUBMITTED")
                // 🔥 SET COLLECTION STATUS FOR APPROVAL WORKFLOW
                .collectionStatus("PENDING_APPROVAL")
                .build();

        VisitLog saved = visitLogRepository.save(visitLog);
        log.info("VisitLog saved with ID: {}, userId: {}, GPS: ({}, {}), collectionStatus: PENDING_APPROVAL",
                saved.getId(), saved.getUserId(), saved.getLatitude(), saved.getLongitude());

        return VisitLogMapper.toResponse(saved);
    }

    /**
     * Case-insensitive key lookup for String values
     */
    private String getValueByKeyIgnoreCase(Map<String, Object> data, String... possibleKeys) {
        if (data == null) return null;

        for (String key : possibleKeys) {
            // Try exact match first
            if (data.containsKey(key)) {
                Object value = data.get(key);
                if (value != null) {
                    String result = value.toString().trim();
                    return result.isEmpty() ? null : result;
                }
            }

            // Try case-insensitive match
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    Object value = entry.getValue();
                    if (value != null) {
                        String result = value.toString().trim();
                        return result.isEmpty() ? null : result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Case-insensitive BigDecimal lookup with multiple key variants
     */
    private BigDecimal getBigDecimalByKeyIgnoreCase(Map<String, Object> data, String... possibleKeys) {
        if (data == null) return null;

        for (String key : possibleKeys) {
            // Try exact match first
            if (data.containsKey(key)) {
                Object value = data.get(key);
                return convertToBigDecimal(value, key);
            }

            // Try case-insensitive match
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key)) {
                    return convertToBigDecimal(entry.getValue(), key);
                }
            }
        }
        return null;
    }

    /**
     * Convert any value to BigDecimal safely
     */
    private BigDecimal convertToBigDecimal(Object value, String key) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Integer) {
                return new BigDecimal((Integer) value);
            }
            if (value instanceof Long) {
                return new BigDecimal((Long) value);
            }
            if (value instanceof Double) {
                return new BigDecimal((Double) value);
            }
            if (value instanceof Float) {
                return new BigDecimal((Float) value);
            }
            String strValue = value.toString().trim();
            if (strValue.isEmpty()) {
                return null;
            }
            return new BigDecimal(strValue);
        } catch (Exception e) {
            log.warn("Could not convert '{}' (value: {}) to BigDecimal: {}",
                    key, value, e.getMessage());
            return null;
        }
    }

    public List<VisitLogResponseDTO> getByAllocationId(Long allocationId) {
        log.info("Fetching visit logs for allocationId: {}", allocationId);
        return visitLogRepository.findByAllocationId(allocationId)
                .stream()
                .map(VisitLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VisitLogResponseDTO> getAll() {
        log.info("Fetching all visit logs");
        List<VisitLogResponseDTO> result = visitLogRepository.findAll()
                .stream()
                .map(VisitLogMapper::toResponse)
                .collect(Collectors.toList());
        log.info("Found {} visit logs", result.size());
        return result;
    }

    private String saveVisitImage(MultipartFile image, Long allocationId) {
        try {
            String baseDir = System.getProperty("user.dir") + "/uploads/visit";
            if (allocationId != null) {
                baseDir += "/" + allocationId;
            }

            Files.createDirectories(Paths.get(baseDir));

            String fileName = UUID.randomUUID() + ".jpg";
            Path fullPath = Paths.get(baseDir, fileName);

            Files.write(fullPath, image.getBytes());

            String relativePath = "/uploads/visit";
            if (allocationId != null) {
                relativePath += "/" + allocationId;
            }
            relativePath += "/" + fileName;

            log.info("Visit image saved: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store visit image", e);
            throw new RuntimeException("Failed to store visit image", e);
        }
    }

    private LocalDate parseVisitDate(String visitDate) {
        if (visitDate == null || visitDate.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(visitDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.error("Invalid visit date format: {}", visitDate);
            throw new IllegalArgumentException("Invalid visit date format: " + visitDate);
        }
    }

    private LocalDate parsePtpDate(String ptpDate) {
        if (ptpDate == null || ptpDate.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(ptpDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.error("Invalid PTP date format: {}", ptpDate);
            throw new IllegalArgumentException("Invalid PTP date format: " + ptpDate);
        }
    }

    public List<VisitLogResponseDTO> getByUserId(Long userId) {
        log.info("Fetching visit logs for userId: {}", userId);
        return visitLogRepository.findByUserId(userId)
                .stream()
                .map(VisitLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}
package com.nimis.chatbot.controller;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.service.AllocationUploadService;
import com.nimis.chatbot.utility.FileUploadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationUploadController {

    private final AllocationUploadService excelUploadService;

    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestPart("file") MultipartFile file) {

        try {
            log.info("Upload request received for file: {}", file.getOriginalFilename());

            // Validate file
            FileUploadValidator.validateFile(file);

            log.info("File validation passed. Processing upload...");
            int inserted = excelUploadService.upload(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File processed successfully");
            response.put("recordsInserted", inserted);

            log.info("Upload completed successfully. Records inserted: {}", inserted);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // ✅ SECURE: Log full error, return sanitized message
            log.warn("Validation error during upload: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", true);
            response.put("message", "Invalid file format or content");
            response.put("type", "VALIDATION_ERROR");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            // ✅ SECURE: Log full stack trace, return generic error
            log.error("Error during file upload: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", true);
            response.put("message", "File upload failed. Please check file format and try again.");
            response.put("type", "UPLOAD_ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ GET BY LOAN NUMBER
    @GetMapping("/{loanNumber}")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> getByLoanNumber(@PathVariable String loanNumber) {
        try {
            Allocation allocation = excelUploadService.getByLoanNumber(loanNumber);
            return ResponseEntity.ok(allocation);
        } catch (RuntimeException e) {
            log.error("Error fetching allocation by loan number {}: {}", loanNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Allocation not found"));
        }
    }

    @GetMapping("/id/{allocationId}")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getAllocationById(@PathVariable Long allocationId) {
        try {
            Allocation allocation = excelUploadService.getById(allocationId);
            return ResponseEntity.ok(allocation);
        } catch (RuntimeException e) {
            log.error("Error fetching allocation by ID {}: {}", allocationId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Allocation not found"));
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN') || hasRole('FO')")
    public ResponseEntity<?> getByUserId(@PathVariable String userId) {
        try {
            log.info("Fetching allocations for userId: {}", userId);
            List<Allocation> allocations = excelUploadService.getByUserId(userId);
            return ResponseEntity.ok(allocations);
        } catch (Exception e) {
            log.error("Error fetching allocations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch allocations"));
        }
    }

    // ✅ GET ALL
    @GetMapping
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getAll() {
        try {
            List<Allocation> allocations = excelUploadService.getAll();
            return ResponseEntity.ok(allocations);
        } catch (Exception e) {
            log.error("Error fetching all allocations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch allocations"));
        }
    }
}
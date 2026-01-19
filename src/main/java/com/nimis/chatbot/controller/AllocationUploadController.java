package com.nimis.chatbot.controller;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.service.AllocationUploadService;
import com.nimis.chatbot.util.FileUploadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            log.warn("Validation error during upload: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", true);
            response.put("message", e.getMessage());
            response.put("type", "VALIDATION_ERROR");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("Error during file upload", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", true);
            response.put("message", e.getMessage() != null ? e.getMessage() : "An error occurred during upload");
            response.put("type", e.getClass().getSimpleName());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ✅ GET BY LOAN NUMBER
    @GetMapping("/{loanNumber}")
    public ResponseEntity<Allocation> getByLoanNumber(
            @PathVariable String loanNumber) {

        return ResponseEntity.ok(
                excelUploadService.getByLoanNumber(loanNumber)
        );
    }

    @GetMapping("/id/{allocationId}")
    public ResponseEntity<Allocation> getAllocationById(
            @PathVariable Long allocationId
    ) {
        return ResponseEntity.ok(
                excelUploadService.getById(allocationId)
        );
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Allocation>> getAll() {
        return ResponseEntity.ok(excelUploadService.getAll());
    }
}
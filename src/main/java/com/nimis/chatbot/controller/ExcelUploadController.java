package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.LoanFullResponse;
import com.nimis.chatbot.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelUploadController {

    private final ExcelUploadService excelUploadService;

    // ================= UPLOAD EXCEL =================
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        try {
            excelUploadService.processExcel(file);
            return ResponseEntity.ok("Excel uploaded and data stored successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================= GET SINGLE LOAN =================
    @GetMapping("/loan/{loanNumber}")
    public ResponseEntity<LoanFullResponse> getLoanByLoanNumber(
            @PathVariable String loanNumber) {

        return ResponseEntity.ok(
                excelUploadService.getLoanByLoanNumber(loanNumber)
        );
    }

    // ================= GET ALL LOANS =================
    @GetMapping("/loans")
    public ResponseEntity<List<LoanFullResponse>> getAllLoans() {

        return ResponseEntity.ok(
                excelUploadService.getAllLoans()
        );
    }
}

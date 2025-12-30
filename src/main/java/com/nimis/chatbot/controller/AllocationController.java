package com.nimis.chatbot.controller;

import com.nimis.chatbot.model.Allocation;
import com.nimis.chatbot.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final ExcelUploadService excelUploadService;

    @PostMapping(
            value = "/upload",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestPart("file") MultipartFile file) throws Exception {

        int inserted = excelUploadService.upload(file);

        return ResponseEntity.ok(
                Map.of(
                        "message", "File processed successfully",
                        "recordsInserted", inserted
                )
        );
    }


    // ✅ GET BY LOAN NUMBER
    @GetMapping("/{loanNumber}")
    public ResponseEntity<Allocation> getByLoanNumber(
            @PathVariable String loanNumber) {

        return ResponseEntity.ok(
                excelUploadService.getByLoanNumber(loanNumber)
        );
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Allocation>> getAll() {
        return ResponseEntity.ok(excelUploadService.getAll());
    }
}

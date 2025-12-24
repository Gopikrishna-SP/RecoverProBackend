package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.AllocationUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        uploadService.uploadExcel(file);
        return ResponseEntity.ok("Excel uploaded successfully");
    }
}

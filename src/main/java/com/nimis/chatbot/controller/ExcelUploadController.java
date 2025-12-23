package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelUploadController {

    private final ExcelUploadService excelUploadService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        excelUploadService.process(file, "ADMIN");
        return "Excel uploaded and processed successfully";
    }
}

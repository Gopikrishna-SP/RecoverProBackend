package com.nimis.chatbot.service;

import com.nimis.chatbot.model.*;
import com.nimis.chatbot.repository.*;
import com.nimis.chatbot.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final LoanAllocationRepository allocationRepo;
    private final UploadFileRepository uploadFileRepo;
    private final UploadErrorRepository errorRepo;

    public void process(MultipartFile file, String user) throws Exception {

        UploadFile upload = new UploadFile();
        upload.setFileName(file.getOriginalFilename());
        upload.setUploadedBy(user);
        upload.setUploadedAt(LocalDateTime.now());
        upload.setStatus("PROCESSING");
        uploadFileRepo.save(upload);

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, String> columnMap = mapHeaders(sheet.getRow(0));

        int success = 0;
        int failed = 0;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            try {
                LoanAllocation allocation = buildAllocation(sheet.getRow(i), columnMap);
                allocation.setSourceFile(upload);
                allocationRepo.save(allocation);
                success++;
            } catch (Exception e) {
                failed++;
                saveError(upload, i, e.getMessage());
            }
        }

        upload.setTotalRows(sheet.getLastRowNum());
        upload.setSuccessRows(success);
        upload.setFailedRows(failed);
        upload.setStatus("COMPLETED");
        uploadFileRepo.save(upload);
    }

    private Map<Integer, String> mapHeaders(Row headerRow) {

        Map<Integer, String> map = new HashMap<>();

        for (Cell cell : headerRow) {
            String normalized = ExcelHeaderUtil.normalize(cell.getStringCellValue());
            if (AllocationHeaderMap.MAP.containsKey(normalized)) {
                map.put(cell.getColumnIndex(), AllocationHeaderMap.MAP.get(normalized));
            }
        }
        return map;
    }

    private LoanAllocation buildAllocation(Row row, Map<Integer, String> columnMap) {

        if (row == null) throw new RuntimeException("Empty row");

        LoanAllocation allocation = new LoanAllocation();

        columnMap.forEach((index, field) -> {
            Cell cell = row.getCell(index);
            if (cell == null) return;

            switch (field) {
                case "loanNumber" -> allocation.setLoanNumber(cell.toString());
                case "customerName" -> allocation.setCustomerName(cell.toString());
                case "product" -> allocation.setProduct(cell.toString());
                case "segment" -> allocation.setSegment(cell.toString());
                case "posAmount" -> allocation.setPosAmount(new BigDecimal(cell.toString()));
                case "emi" -> allocation.setEmi(new BigDecimal(cell.toString()));
                case "branch" -> allocation.setBranch(cell.toString());
                case "bucket" -> allocation.setBucket(cell.toString());
            }
        });

        if (allocation.getLoanNumber() == null || allocation.getLoanNumber().isEmpty()) {
            throw new RuntimeException("Loan number missing");
        }

        return allocation;
    }

    private void saveError(UploadFile file, int row, String message) {

        UploadError error = new UploadError();
        error.setUploadFile(file);
        error.setRowNumber((long) row);
        error.setErrorMessage(message);
        errorRepo.save(error);
    }
}

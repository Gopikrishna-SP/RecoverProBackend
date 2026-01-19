package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationUploadService {

    private final AllocationRepository allocationRepository;

    public int upload(MultipartFile file) throws Exception {
        log.info("Starting upload for file: {}", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        int inserted = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            log.info("Workbook created successfully");

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("No sheet found in workbook");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("No header row found");
            }

            log.info("Processing {} rows", sheet.getLastRowNum());

            // Process each data row
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                try {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;

                    Map<String, Object> rowData = new LinkedHashMap<>();
                    String loanNumber = null;

                    // Read all cells in the row
                    for (Cell cell : row) {
                        String columnName = getHeaderName(headerRow, cell.getColumnIndex());
                        if (columnName != null && !columnName.isBlank()) {
                            Object value = readCell(cell);
                            rowData.put(columnName, value);

                            if (columnName.equalsIgnoreCase("LOANNUMBER")) {
                                loanNumber = value != null ? value.toString().trim() : null;
                            }
                        }
                    }

                    // Skip row if no loan number
                    if (loanNumber == null || loanNumber.isBlank()) {
                        log.warn("Row {} has no loan number, skipping", r);
                        continue;
                    }

                    // Check if loan already exists - UPDATE if duplicate, INSERT if new
                    var existingAllocation = allocationRepository.findByLoanNumber(loanNumber);

                    Allocation allocation;
                    if (existingAllocation.isPresent()) {
                        // Update existing allocation
                        allocation = existingAllocation.get();
                        allocation.setAllocationData(rowData);
                        allocation.setUpdatedAt(java.time.LocalDateTime.now());
                        log.info("Updating existing allocation: {}", loanNumber);
                    } else {
                        // Create new allocation
                        allocation = new Allocation();
                        allocation.setLoanNumber(loanNumber);
                        allocation.setAllocationData(rowData);
                        allocation.setStatus("UNASSIGNED");
                        allocation.setVisitCount(0);
                        log.info("Creating new allocation: {}", loanNumber);
                    }

                    try {
                        allocationRepository.save(allocation);
                        inserted++;
                        log.info("Successfully saved allocation: {}", loanNumber);
                    } catch (Exception e) {
                        log.error("Failed to save allocation {}: {}", loanNumber, e.getMessage());
                        // Continue to next row instead of crashing
                        continue;
                    }

                } catch (Exception e) {
                    log.error("Error processing row {}: {}", r, e.getMessage());
                    // Continue to next row
                }
            }

            log.info("Upload completed. {} records inserted", inserted);
            return inserted;

        } catch (Exception e) {
            log.error("Error during file upload", e);
            throw e;
        }
    }

    private String getHeaderName(Row headerRow, int columnIndex) {
        try {
            Cell cell = headerRow.getCell(columnIndex);
            if (cell != null) {
                String value = cell.getStringCellValue();
                return value != null ? value.trim() : null;
            }
        } catch (Exception e) {
            log.warn("Error reading header at column {}: {}", columnIndex, e.getMessage());
        }
        return null;
    }

    private Object readCell(Cell cell) {
        try {
            if (cell == null) return null;

            switch (cell.getCellType()) {
                case STRING:
                    String str = cell.getStringCellValue();
                    return str != null ? str.trim() : null;

                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toLocalDate();
                    } else {
                        return cell.getNumericCellValue();
                    }

                case BOOLEAN:
                    return cell.getBooleanCellValue();

                case BLANK:
                    return null;

                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("Error reading cell: {}", e.getMessage());
            return null;
        }
    }

    public Allocation getByLoanNumber(String loanNumber) {
        return allocationRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));
    }

    public Allocation getById(Long allocationId) {
        return allocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Allocation not found with id: " + allocationId));
    }

    public List<Allocation> getAll() {
        return allocationRepository.findAll();
    }
}
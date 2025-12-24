package com.nimis.chatbot.service;

import com.nimis.chatbot.model.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import com.nimis.chatbot.util.ExcelHeaderMap;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AllocationUploadService {

    private final AllocationRepository allocationRepository;

    public void uploadExcel(MultipartFile file) {

        List<Allocation> allocations = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<Integer, String> columnFieldMap = new HashMap<>();

            // 🔹 Map Excel column index → Entity field
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim().toUpperCase();
                if (ExcelHeaderMap.HEADER_MAP.containsKey(header)) {
                    columnFieldMap.put(cell.getColumnIndex(),
                            ExcelHeaderMap.HEADER_MAP.get(header));
                }
            }

            // 🔹 Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Allocation allocation = new Allocation();

                for (Map.Entry<Integer, String> entry : columnFieldMap.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    if (cell == null) continue;

                    setFieldValue(allocation, entry.getValue(), cell);
                }

                allocations.add(allocation);
            }

            allocationRepository.saveAll(allocations);

        } catch (Exception e) {
            throw new RuntimeException("Excel upload failed", e);
        }
    }

    // 🔹 Safe reflection setter
    private void setFieldValue(Allocation allocation, String fieldName, Cell cell) {
        try {
            Field field = Allocation.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().equals(Double.class)) {
                field.set(allocation, cell.getNumericCellValue());
            } else {
                cell.setCellType(CellType.STRING);
                field.set(allocation, cell.getStringCellValue().trim());
            }

        } catch (Exception ignored) {
        }
    }
}

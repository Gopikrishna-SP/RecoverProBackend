package com.nimis.chatbot.service;

import com.nimis.chatbot.model.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {

    private final AllocationRepository allocationRepository;

    // ✅ CLEANED & DEDUPLICATED HEADERS
    private static final Set<String> ALLOWED_HEADERS = new LinkedHashSet<>(List.of(
            "SEGMENT","PRODUCT","ZONE","STATE","BRANCH","LOCATION","LOANNUMBER",
            "CUSTOMER NAME","DISBURSED AMOUNT (IN CR)","DISBURSED DATE",
            "POS (IN CR)","POS Amt","EMI","EMI START DATE","EMI END DATE",
            "BKT TAG","OPENING BKT","ASHV DA/PTC","SECURITIZATION","SE/INSE",
            "AGENCY CODE","AGENCY","MANAGER EMP ID","MANAGER","ZM EMP ID","ZONAL MANAGER",

            "Main_Applicant_Mobile_No","Main_applicant_Name",
            "Co_Applicant1_Name","Co_Applicant1_Mobile_No","Relation_with_Main_Applicant",

            "address_priority_1","address_priority_2","address_priority_3","address_priority_4",
            "address_priority_5","address_priority_6","address_priority_7","address_priority_8",

            "business_pin_code","residence_pin_code","main_pincode",

            "pan_main_app","dob_main_app","pan_co_app","dob_co_app",

            "address_1","address_2","address_3","address_4","address_5",
            "address_6","address_7","address_8","address_9","address_10",

            "phone_1","phone_2","phone_3","phone_4","phone_5",
            "phone_6","phone_7","phone_8","phone_9","phone_10",

            "MONTH - LAST NOTICE","LRN 1","LRN MONTH 2","LRN 2","LRN MONTH 3","LRN 3",

            "REVISED STAGE IN ARBITRATION (20.11.2025)",
            "ADVOCATE ON RECORD (20.11.2025)",
            "ARBITRATION INVOKATION DATE  1   (20.11.2025)",
            "ARBITRATION INVOKATION DATE  2  (20.11.2025)",
            "TENTATIVE DATE TO ISSUE REFERENCE (20.11.2025)",
            "LETTER TO ARBITRATOR (20.11.2025)",
            "TENTATIVE DATE FOR FREEZING AND OTHER ORDERS (20.11.2025)",
            "SEC 17 - ORDER DATE (20.11.2025)",
            "RELIEF (20.11.2025)",

            "NOTICE DATE","MONTH OF NOTICE","Date of Filling Confrmation",

            "SEC 25- FILED/NOT FILED","SEC 25 -  PROCESS STAGE",
            "SEC 25 -  LDOH","SEC 25 -  NDOH",

            "NODH","LISTING DATE","DATE OF FILING","COURT/FORUM","Case Number",

            "ADVOCATE NAME","ADVOCATE'S CONTACT NUMBER","AUTHORIZED OFFICER",

            "STAGE 1 - FOR VERIFICATION","STAGE 1 - HEARING ON",
            "STAGE 2 - SUMMONS STAGE",
            "STAGE 2 - SUMMONS ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 2 - SUMMONS COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",

            "STAGE 3 - APPERANCE/ NON APPEARANCE","STAGE 3 - HEARING ON",

            "STAGE 4 - BAILABLE WARRANT ISSUED DATE",
            "STAGE 4 - BAILABLE WARRANT ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 4 - BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",

            "STAGE 5 - NON BAILABLE WARRANT ISSUED DATE",
            "BW RE ISSUED DATE","BW WARRANT POST DATE","BW RE ISSUED COLLECTION DATE",
            "STAGE 5 - NON BAILABLE WARRANT ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 5 - NON BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",

            "STAGE 6 - NON BAILABLE WARRANT REISSUED DATE",
            "STAGE 6 - NON BAILABLE WARRANT REISSUED / COLLECTED/ DISPATCHED",
            "STAGE 7 - NON BAILABLE WARRANT REISSUED DATE",
            "STAGE 7 - NON BAILABLE WARRANT REISSUED / COLLECTED/ DISPATCHED",
            "STAGE 8 - NON BAILABLE WARRANT REISSUED DATE",
            "STAGE 8 - NON BAILABLE WARRANT REISSUED / COLLECTED/ DISPATCHED",

            "NON-BAILABLE WARRANT RE-ISSUED DATE",
            "NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "2ND NON-BAILABLE WARRANT RE-ISSUED DATE",
            "2ND NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "3RD NON-BAILABLE WARRANT RE-ISSUED DATE",
            "3RD NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "4RD NON-BAILABLE WARRANT RE-ISSUED DATE",
            "4RD NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "5RD NON-BAILABLE WARRANT RE-ISSUED DATE",
            "5RD NON-BAILABLE WARRANT REISSUE COLLX DATE",

            "STAGE 5 - PROCLAMATION ISSUED DATE",
            "STAGE 5 - PROCLAMATION ISSUED/ COLLECTED/ DISPATCHED",
            "STAGE 5 - PROCLAMATION COLLECTED / NOT COLLECTED",
            "STAGE 5- ATTACHMENT OF PROPERTY ISSUED DATE",
            "STAGE 5- ATTACHMENT OF PROPERTY ISSUED/ COLLECTED",
            "STAGE 5 - ATTACHMENT OF PROPERTY COLLECTED/ NOT COLLECTED",
            "STAGE 5 - NON BAILABLE WARRANT RECOLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",
            "Account Holder 1","BANK NAME 1","ACCOUNT NUMBER 1","IFSC CODE 1",
            "Account Holder 2","BANK NAME 2","ACCOUNT NUMBER 2","IFSC CODE 2",
            "Account Holder 3","BANK NAME 3","ACCOUNT NUMBER 3","IFSC CODE 3"
    ));

    // Default values for specific fields to ensure they are saved and returned when missing
    private static final Map<String, Object> DEFAULT_VALUES = new LinkedHashMap<>();
    static {
        DEFAULT_VALUES.put("LISTING DATE", "-");
        DEFAULT_VALUES.put("DATE OF FILING", "-");
        DEFAULT_VALUES.put("COURT/FORUM", "-");
        DEFAULT_VALUES.put("Case Number", "AWAITED");
        DEFAULT_VALUES.put("ADVOCATE NAME", "-");
        DEFAULT_VALUES.put("ADVOCATE'S CONTACT NUMBER", "-");
        DEFAULT_VALUES.put("AUTHORIZED OFFICER", "-");
        DEFAULT_VALUES.put("STAGE 1 - FOR VERIFICATION", "-");
        DEFAULT_VALUES.put("STAGE 1 - HEARING ON", "-");
        DEFAULT_VALUES.put("STAGE 2 - SUMMONS STAGE", "-");
        DEFAULT_VALUES.put("STAGE 2 - SUMMONS COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED", "-");
        DEFAULT_VALUES.put("STAGE 3 - APPERANCE/ NON APPEARANCE", "-");
        DEFAULT_VALUES.put("STAGE 3 - HEARING ON", "-");
        DEFAULT_VALUES.put("STAGE 4 - BAILABLE WARRANT ISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 4 - BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED", "-");
        DEFAULT_VALUES.put("LOCATION FILLING", "-");
        DEFAULT_VALUES.put("CASE NUMBER", "-");
        DEFAULT_VALUES.put("Case Withdrawal Date", "-");
        DEFAULT_VALUES.put("REMARKS", "-");
        DEFAULT_VALUES.put("CLAIM AMOUNT", "-");
        DEFAULT_VALUES.put("BW RE ISSUED DATE", "-");
        DEFAULT_VALUES.put("BW RE ISSUED COLLECTION DATE", "-");
        DEFAULT_VALUES.put("STAGE 5 - NON BAILABLE WARRANT ISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 5 - NON BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED", "-");
        DEFAULT_VALUES.put("STAGE 6 - NON BAILABLE WARRANT REISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 7 - NON BAILABLE WARRANT REISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 8 - NON BAILABLE WARRANT REISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 5 - PROCLAMATION ISSUED DATE", "-");
        DEFAULT_VALUES.put("STAGE 5 - PROCLAMATION ISSUED/ COLLECTED/ DISPATCHED", "-");
        DEFAULT_VALUES.put("STAGE 5 - PROCLAMATION COLLECTED / NOT COLLECTED", "-");
        DEFAULT_VALUES.put("STAGE 5 - ATTACHMENT OF PROPERTY COLLECTED/ NOT COLLECTED", "-");
        DEFAULT_VALUES.put("Account Holder 1", "MEDICENE POINT Through Its Proprietor");
        DEFAULT_VALUES.put("BANK NAME 1", "AXIS BANK, INDIA");
        DEFAULT_VALUES.put("ACCOUNT NUMBER 1", "915020014111122");
        DEFAULT_VALUES.put("IFSC CODE 1", "UTIB0000094");
        DEFAULT_VALUES.put("Account Holder 2", 0);
        DEFAULT_VALUES.put("BANK NAME 2", 0);
        DEFAULT_VALUES.put("ACCOUNT NUMBER 2", 0);
        DEFAULT_VALUES.put("IFSC CODE 2", 0);
        DEFAULT_VALUES.put("Account Holder 3", 0);
        DEFAULT_VALUES.put("BANK NAME 3", 0);
        DEFAULT_VALUES.put("ACCOUNT NUMBER 3", 0);
        DEFAULT_VALUES.put("IFSC CODE 3", 0);
        DEFAULT_VALUES.put("STAGE 5 - NON BAILABLE WARRANT RECOLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED", "-");
    }

    // Exact output order requested (deduplicated while preserving order)
    private static final List<String> OUTPUT_HEADERS = new ArrayList<>(new LinkedHashSet<>(List.of(
            "SEGMENT","PRODUCT","ZONE","STATE","BRANCH","LOCATION","LOANNUMBER",
            "CUSTOMER NAME","DISBURSED AMOUNT (IN CR)","DISBURSED DATE","POS (IN CR)",
            "POS Amt","EMI","EMI START DATE","EMI END DATE","BKT TAG","OPENING BKT",
            "ASHV DA/PTC","SECURITIZATION","SE/INSE","AGENCY CODE","AGENCY","MANAGER EMP ID",
            "MANAGER","ZM EMP ID","ZONAL MANAGER","Main_Applicant_Mobile_No","Main_applicant_Name",
            "Co_Applicant1_Name","Co_Applicant1_Mobile_No","Relation_with_Main_Applicant",
            "address_priority_1","address_priority_2","address_priority_3","address_priority_4",
            "address_priority_5","address_priority_6","address_priority_7","address_priority_8",
            "business_pin_code","residence_pin_code","main_pincode","pan_main_app","dob_main_app",
            "pan_co_app","dob_co_app","address_1","address_2","address_3","address_4","address_5",
            "address_6","address_7","address_8","address_9","address_10","phone_1","phone_2",
            "phone_3","phone_4","phone_5","phone_6","phone_7","phone_8","phone_9","phone_10",
            "MONTH - LAST NOTICE","LRN 1","LRN MONTH 2","LRN 2","LRN MONTH 3","LRN 3",
            "REVISED STAGE IN ARBITRATION (20.11.2025)","ADVOCATE ON RECORD (20.11.2025)",
            "ARBITRATION INVOKATION DATE  1   (20.11.2025)","ARBITRATION INVOKATION DATE  2  (20.11.2025)",
            "TENTATIVE DATE TO ISSUE REFERENCE (20.11.2025)","LETTER TO ARBITRATOR (20.11.2025)",
            "TENTATIVE DATE FOR FREEZING AND OTHER ORDERS (20.11.2025)","SEC 17 - ORDER DATE (20.11.2025)",
            "RELIEF (20.11.2025)","NODH","MONTH OF NOTICE","NOTICE DATE","Date of Filling Confrmation",
            "SEC 25- FILED/NOT FILED","SEC 25 -  PROCESS STAGE","SEC 25 -  LDOH","SEC 25 -  NDOH",
            "LISTING DATE","DATE OF FILING","COURT/FORUM","Case Number","ADVOCATE NAME",
            "ADVOCATE'S CONTACT NUMBER","AUTHORIZED OFFICER","STAGE 1 - FOR VERIFICATION",
            "STAGE 1 - HEARING ON","STAGE 2 - SUMMONS STAGE","STAGE 2 -SUMMONS  ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 2 - SUMMONS COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",
            "STAGE 3 - APPERANCE/ NON APPEARANCE","STAGE 3 - HEARING ON","STAGE 4 - BAILABLE WARRANT ISSUED DATE",
            "STAGE 4 -BAILABLE WARRANT  ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 4 - BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",
            "STAGE 6 - NON BAILABLE WARRANT  ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 6 - NON BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",
            "NON-BAILABLE WARRANT RE-ISSUED DATE","NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "2ND NON-BAILABLE WARRANT RE-ISSUED DATE","2ND NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "3RD NON-BAILABLE WARRANT RE-ISSUED DATE","3RD NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "4RD NON-BAILABLE WARRANT RE-ISSUED DATE","4RD NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "5RD NON-BAILABLE WARRANT RE-ISSUED DATE","5RD NON-BAILABLE WARRANT REISSUE COLLX DATE",
            "SEC 138 - FILED/NOT FILED","SEC 138 -  PROCESS STAGE","SEC 138 -  LDOH","SEC 138 -  NDOH",
            "LOCATION FILLING","CASE NUMBER","SEC 420 -  PROCESS STAGE","Case Withdrawal Date",
            "SEC 420 -  LDOH","SEC 420 -  NDOH","REMARKS","CLAIM AMOUNT","BW RE ISSUED DATE",
            "BW  WARRANT POST DATE","BW RE ISSUED COLLECTION DATE",
            "STAGE 5 - NON BAILABLE WARRANT ISSUED DATE","STAGE 5 - NON BAILABLE WARRANT  ISSUED / COLLECTED/ DISPATCHED",
            "STAGE 5 - NON BAILABLE WARRANT COLLECTED/ NOT COLLECTED/ DISPATCHED/ NOT DISPATCHED",
            "STAGE 6 - NON BAILABLE WARRANT REISSUED DATE","STAGE 6 - NON BAILABLE WARRANT  REISSUED / COLLECTED/ DISPATCHED",
            "STAGE 7 - NON BAILABLE WARRANT REISSUED DATE","STAGE 7 - NON BAILABLE WARRANT  REISSUED / COLLECTED/ DISPATCHED",
            "STAGE 8 - NON BAILABLE WARRANT REISSUED DATE","STAGE 8 - NON BAILABLE WARRANT  REISSUED / COLLECTED/ DISPATCHED",
            "STAGE 5 - PROCLAMATION ISSUED DATE","STAGE 5 - PROCLAMATION ISSUED/ COLLECTED/ DISPATCHED",
            "STAGE 5 - PROCLAMATION COLLECTED / NOT COLLECTED",
            "STAGE 5- ATTACHMENT OF PROPERTY ISSUED DATE","STAGE 5- ATTACHMENT OF PROPERTY ISSUED/ COLLECTED",
            "STAGE 5 - ATTACHMENT OF PROPERTY COLLECTED/ NOT COLLECTED",
            "Account Holder 1","BANK NAME 1","ACCOUNT NUMBER 1","IFSC CODE 1",
            "Account Holder 2","BANK NAME 2","ACCOUNT NUMBER 2","IFSC CODE 2",
            "Account Holder 3","BANK NAME 3","ACCOUNT NUMBER 3","IFSC CODE 3"
    )));

    // ================= UPLOAD =================
    public int upload(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // Build normalized lookup for allowed headers -> original allowed header
            Map<String, String> normalizedAllowed = new HashMap<>();
            for (String h : ALLOWED_HEADERS) {
                normalizedAllowed.put(normalizeHeader(h), h);
            }

            // Map found sheet headers to allowed header keys (header -> columnIndex)
            Map<String, Integer> headerToIndex = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String raw = cell.getStringCellValue();
                String normalized = normalizeHeader(raw);
                if (normalizedAllowed.containsKey(normalized)) {
                    headerToIndex.put(normalizedAllowed.get(normalized), cell.getColumnIndex());
                }
            }

            int inserted = 0;

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                // Build rowData in ALLOWED_HEADERS order (only include headers present on sheet)
                Map<String, Object> rowData = new LinkedHashMap<>();
                String loanNumber = null;

                for (String header : ALLOWED_HEADERS) {
                    if (headerToIndex.containsKey(header)) {
                        Object value = readCell(row.getCell(headerToIndex.get(header)));
                        rowData.put(header, value);
                        if ("LOANNUMBER".equals(header)) {
                            loanNumber = value != null ? value.toString() : null;
                        }
                    }
                }

                // Ensure required defaults are present (even if sheet doesn't contain those columns)
                for (Map.Entry<String, Object> def : DEFAULT_VALUES.entrySet()) {
                    rowData.putIfAbsent(def.getKey(), def.getValue());
                }

                if (loanNumber == null || loanNumber.isBlank()) continue;

                Allocation allocation = new Allocation();
                allocation.setLoanNumber(loanNumber);
                allocation.setAllocationData(rowData);
                allocationRepository.save(allocation);
                inserted++;
            }
            return inserted;
        }
    }

    public Allocation getByLoanNumber(String loanNumber) {
        Allocation allocation = allocationRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanNumber));
        orderAllocationData(allocation);
        return allocation;
    }

    public List<Allocation> getAll() {
        List<Allocation> all = allocationRepository.findAll();
        for (Allocation a : all) orderAllocationData(a);
        return all;
    }

    // Reorders allocationData map to follow OUTPUT_HEADERS order (keeps only present keys),
    // then appends any additional keys that weren't defined in OUTPUT_HEADERS (to avoid data loss).
    private void orderAllocationData(Allocation allocation) {
        if (allocation == null || allocation.getAllocationData() == null) return;
        Map<String, Object> original = allocation.getAllocationData();
        Map<String, Object> ordered = new LinkedHashMap<>();
        // Add headers in specified order: prefer stored value; if missing/null use default if available
        for (String header : OUTPUT_HEADERS) {
            if (original.containsKey(header) && original.get(header) != null) {
                ordered.put(header, original.get(header));
            } else if (DEFAULT_VALUES.containsKey(header)) {
                ordered.put(header, DEFAULT_VALUES.get(header));
            }
        }
        // Append any remaining keys that weren't in the output list (keep stored values)
        for (Map.Entry<String, Object> e : original.entrySet()) {
            if (!ordered.containsKey(e.getKey())) {
                ordered.put(e.getKey(), e.getValue());
            }
        }
        // Append any default keys not yet present (to ensure those defaults show up)
        for (Map.Entry<String, Object> def : DEFAULT_VALUES.entrySet()) {
            if (!ordered.containsKey(def.getKey())) {
                ordered.put(def.getKey(), def.getValue());
            }
        }
        allocation.setAllocationData(ordered);
    }

    // Normalizes headers for tolerant matching (trim + collapse spaces + uppercase)
    private String normalizeHeader(String header) {
        if (header == null) return "";
        return header.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    private Object readCell(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate()
                    : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> null;
        };
    }
}

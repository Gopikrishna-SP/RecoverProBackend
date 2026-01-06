package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.FieldExecutiveCaseResponse;
import com.nimis.chatbot.dto.response.FieldExecutiveDashboardCaseResponse;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class FieldExecutiveService {

    private final AllocationRepository allocationRepository;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "ASSIGNED",
            "VISITED",
            "PROMISE_TO_PAY",
            "PAYMENT_COLLECTED",
            "NOT_REACHABLE"
    );

    public List<FieldExecutiveDashboardCaseResponse> getDashboardCases(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        List<Object[]> rows = allocationRepository.findDashboardCases(
                userId,
                List.of("ASSIGNED", "VISITED", "PROMISE_TO_PAY")
        );

        return rows.stream()
                .map(r -> new FieldExecutiveDashboardCaseResponse(
                        (String) r[0], // loanNumber
                        (String) r[1], // customerName
                        (String) r[2]  // location
                ))
                .toList();
    }


    public List<FieldExecutiveCaseResponse> getMyCases(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        List<Allocation> allocations =
                allocationRepository.findByFieldExecutiveIdAndStatusIn(
                        userId,
                        List.of("ASSIGNED", "VISITED", "PROMISE_TO_PAY")
                );

        return allocations.stream()
                .map(a -> {
                    Map<String, Object> d = a.getAllocationData();

                    return FieldExecutiveCaseResponse.builder()
                            .segment(getString(d, "SEGMENT"))
                            .location(getString(d, "LOCATION"))
                            .loanNumber(a.getLoanNumber())
                            .customerName(getString(d, "CUSTOMER NAME"))
                            .posInCr(getDouble(d, "POS (IN CR)"))
                            .posAmount(getDouble(d, "POS Amt"))
                            .emi(getInt(d, "EMI"))
                            .emiOverdue(getInt(d, "Emi Overdue"))
                            .mobile(getString(d, "phone_1"))
                            .emiDueCount(getInt(d, "Count Of Emi Due NOV"))
                            .bktTag(getString(d, "BKT TAG"))
                            .openingBucket(getString(d, "OPENING BKT"))
                            .securitization(getString(d, "SECURITIZATION"))
                            .ashvDaPtc(getString(d, "ASHV DA/PTC"))
                            .warrant(getString(d, "Warrant"))
                            .coApplicant1Name(getString(d, "Co_Applicant1_Name"))
                            .coApplicant1Mobile(getString(d, "Co_Applicant1_Mobile_No"))
                            .addressPriority1(getString(d, "address_priority_1"))
                            .addressPriority2(getString(d, "address_priority_2"))
                            .addressPriority3(getString(d, "address_priority_3"))
                            .addressPriority4(getString(d, "address_priority_4"))
                            .build();
                })
                .toList();
    }

    /* ---------- SAFE MAP READERS ---------- */

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? null : v.toString();
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number n) return n.intValue();
        try { return v == null ? null : Integer.parseInt(v.toString()); }
        catch (Exception e) { return null; }
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v instanceof Number n) return n.doubleValue();
        try { return v == null ? null : Double.parseDouble(v.toString()); }
        catch (Exception e) { return null; }
    }


    public List<String> getVisitAddresses(Long userId, String loanNumber) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (loanNumber == null || loanNumber.isBlank()) {
            throw new IllegalArgumentException("loanNumber is required");
        }

        Allocation allocation = allocationRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!Objects.equals(allocation.getFieldExecutiveId(), userId)) {
            throw new AccessDeniedException("Unauthorized access");
        }

        Map<String, Object> data = allocation.getAllocationData();
        if (data == null || data.isEmpty()) {
            return List.of();
        }

        return data.entrySet().stream()
                // pick only address fields (18 address columns)
                .filter(e -> e.getKey().toLowerCase().startsWith("address"))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)

                // normalize values (fix 560064.0, numeric values)
                .map(value -> {
                    if (value instanceof Number num) {
                        long longVal = num.longValue();
                        if (num.doubleValue() == longVal) {
                            return String.valueOf(longVal); // removes .0
                        }
                        return num.toString();
                    }
                    return value.toString();
                })

                // remove "0-9. " prefix like "3. Registered Address"
                .map(s -> s.replaceFirst("^\\d+\\.\\s*", "").trim())

                // clean junk values
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.equalsIgnoreCase("0"))
                .filter(s -> !s.equals("-"))

                // unique addresses only
                .distinct()
                .toList();
    }





}

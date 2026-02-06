package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.BankAdminDashboardResponse;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;
import com.nimis.chatbot.repository.AllocationRepository;
import com.nimis.chatbot.repository.VisitLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAdminService {

    private final AllocationRepository allocationRepository;
    private final VisitLogRepository visitLogRepository;

    /**
     * Get Bank Admin Dashboard Stats
     */
    public BankAdminDashboardResponse getDashboardStats() {
        log.info("=== Calculating Bank Admin Dashboard Stats ===");

        try {
            List<Allocation> allocations = allocationRepository.findAll();
            log.info("Found {} allocations", allocations.size());

            // Total Case Value = SUM of POS from allocationData JSON
            BigDecimal totalCaseValue = calculateTotalCaseValue(allocations);
            log.info("Total Case Value (POS): {}", totalCaseValue);

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            // Collection metrics (APPROVED only)
            BigDecimal todayCollection = calculateApprovedCollectionByDate(today);
            BigDecimal yesterdayCollection = calculateApprovedCollectionByDate(yesterday);
            BigDecimal totalCollection = calculateApprovedCollectionTotal();

            log.info("Today's Collection: {}", todayCollection);
            log.info("Yesterday's Collection: {}", yesterdayCollection);
            log.info("Total Collection: {}", totalCollection);

            // Pending For Approval = PENDING_APPROVAL amounts only
            BigDecimal pendingForApproval = calculatePendingApprovalCollection();
            log.info("Pending For Approval: {}", pendingForApproval);

            return BankAdminDashboardResponse.builder()
                    .totalCaseValue(totalCaseValue)
                    .totalCollection(totalCollection)
                    .todayCollections(todayCollection)
                    .yesterdayCollection(yesterdayCollection)
                    .monthlyCollection(totalCollection)
                    .totalUnapprovedCash(pendingForApproval)
                    .cashPendingForDeposit(BigDecimal.ZERO)
                    .pendingForApproval(pendingForApproval)
                    .build();

        } catch (Exception e) {
            log.error("Error calculating dashboard stats", e);
            return getDefaultStats();
        }
    }

    /**
     * Total Case Value = SUM of POS from allocationData JSON
     * Extracts POS_IN_CR, POS (IN CR), or POS from the JSON map
     */
    private BigDecimal calculateTotalCaseValue(List<Allocation> allocations) {
        BigDecimal total = allocations.stream()
                .map(this::extractPosFromJson)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Total Case Value calculated: {}", total);
        return total;
    }

    /**
     * Extract POS value from allocationData JSON (case-insensitive)
     * Tries: POS_IN_CR, POS (IN CR), POS
     */
    private BigDecimal extractPosFromJson(Allocation allocation) {
        if (allocation == null || allocation.getAllocationData() == null) {
            return null;
        }

        Map<String, Object> data = allocation.getAllocationData();

        // Try exact matches first
        String[] possibleKeys = {"POS_Amt", "POS Amt", "POS", "pos_amt", "pos Amt", "Amt"};

        for (String key : possibleKeys) {
            if (data.containsKey(key)) {
                return convertToBigDecimal(data.get(key));
            }
        }

        // Try case-insensitive match
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey() != null &&
                    (entry.getKey().equalsIgnoreCase("POS_Amt") ||
                            entry.getKey().equalsIgnoreCase("POS Amt")
                    )) {
                return convertToBigDecimal(entry.getValue());
            }
        }

        return null;
    }

    /**
     * Today's Collection = APPROVED visit logs from today only
     */
    private BigDecimal calculateApprovedCollectionByDate(LocalDate date) {
        List<VisitLog> visits = visitLogRepository.findByVisitDate(date);
        BigDecimal total = visits.stream()
                .filter(v -> "APPROVED".equals(v.getCollectionStatus()))
                .filter(v -> v.getAmount() != null)
                .filter(v -> v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Approved collection for {}: {}", date, total);
        return total;
    }

    /**
     * Total Collection = ALL APPROVED visit logs (monthly/cumulative)
     */
    private BigDecimal calculateApprovedCollectionTotal() {
        List<VisitLog> visits = visitLogRepository.findAll();
        BigDecimal total = visits.stream()
                .filter(v -> "APPROVED".equals(v.getCollectionStatus()))
                .filter(v -> v.getAmount() != null)
                .filter(v -> v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Total approved collection: {}", total);
        return total;
    }

    /**
     * Pending For Approval = ONLY PENDING_APPROVAL status
     * 🔥 FIXED: Now only counts PENDING_APPROVAL status
     */
    private BigDecimal calculatePendingApprovalCollection() {
        List<VisitLog> visits = visitLogRepository.findByCollectionStatus("PENDING_APPROVAL");
        BigDecimal total = visits.stream()
                .filter(v -> v.getAmount() != null)
                .filter(v -> v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Pending approval collection: {}", total);
        return total;
    }

    /**
     * Convert value to BigDecimal safely
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Integer) {
                return new BigDecimal((Integer) value);
            }
            if (value instanceof Long) {
                return new BigDecimal((Long) value);
            }
            if (value instanceof Double) {
                return new BigDecimal((Double) value);
            }
            if (value instanceof Float) {
                return new BigDecimal((Float) value);
            }
            String strValue = value.toString().trim();
            if (strValue.isEmpty()) {
                return null;
            }
            return new BigDecimal(strValue);
        } catch (Exception e) {
            log.warn("Could not convert value to BigDecimal: {}", value);
            return null;
        }
    }

    /**
     * Get default stats
     */
    private BankAdminDashboardResponse getDefaultStats() {
        return BankAdminDashboardResponse.builder()
                .totalCaseValue(BigDecimal.ZERO)
                .totalCollection(BigDecimal.ZERO)
                .todayCollections(BigDecimal.ZERO)
                .yesterdayCollection(BigDecimal.ZERO)
                .monthlyCollection(BigDecimal.ZERO)
                .totalUnapprovedCash(BigDecimal.ZERO)
                .cashPendingForDeposit(BigDecimal.ZERO)
                .pendingForApproval(BigDecimal.ZERO)
                .build();
    }

    // ==================== COLLECTION APPROVAL ====================

    /**
     * Get pending collections
     * 🔥 FIXED: Now only returns PENDING_APPROVAL status
     */
    public List<VisitLog> getPendingCollections() {
        List<VisitLog> pending = visitLogRepository.findByCollectionStatus("PENDING_APPROVAL");
        return pending != null ? pending : new ArrayList<>();
    }

    /**
     * Approve collection (PENDING_APPROVAL -> APPROVED)
     * 🔥 FIXED: Changed from PENDING_DEPOSIT to APPROVED
     */
    public VisitLog approveCollection(Long id, String approvedBy) {
        VisitLog log = visitLogRepository.findById(id).orElse(null);
        if (log != null && "PENDING_APPROVAL".equals(log.getCollectionStatus())) {
            log.setCollectionStatus("APPROVED");
            log.setApprovedBy(approvedBy);
            log.setApprovedAt(java.time.LocalDateTime.now());
            return visitLogRepository.save(log);
        }
        return log;
    }

    /**
     * Reject collection
     */
    public VisitLog rejectCollection(Long id, String rejectedBy, String reason) {
        VisitLog log = visitLogRepository.findById(id).orElse(null);
        if (log != null && "PENDING_APPROVAL".equals(log.getCollectionStatus())) {
            log.setCollectionStatus("REJECTED");
            log.setApprovedBy(rejectedBy);
            log.setApprovedAt(java.time.LocalDateTime.now());
            log.setRejectionReason(reason);
            return visitLogRepository.save(log);
        }
        return log;
    }

    /**
     * Mark collection as deposited (APPROVED -> DEPOSITED)
     * 🔥 FIXED: Changed from PENDING_DEPOSIT to APPROVED as prerequisite
     */
    public VisitLog markAsDeposited(Long id, String depositedBy) {
        VisitLog log = visitLogRepository.findById(id).orElse(null);
        if (log != null && "APPROVED".equals(log.getCollectionStatus())) {
            log.setCollectionStatus("DEPOSITED");
            log.setDepositedAt(java.time.LocalDateTime.now());
            return visitLogRepository.save(log);
        }
        return log;
    }
}
package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.AllocationDTO;
import com.nimis.chatbot.dto.response.VendorDashboardResponse;
import com.nimis.chatbot.dto.response.FieldOfficerDTO;
import com.nimis.chatbot.dto.response.FieldExecutiveDashboardCaseResponse;
import com.nimis.chatbot.dto.response.FieldExecutiveCaseResponse;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.AllocationRepository;
import com.nimis.chatbot.repository.VisitLogRepository;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VisitLogRepository visitLogRepository;
    private final AllocationRepository allocationRepository;
    private final UserRepository userRepository;

    /**
     * Get dashboard cases (same as FieldExecutive format)
     * Returns ASSIGNED cases with borrower details
     */
    public List<FieldExecutiveDashboardCaseResponse> getDashboardCases() {
        log.info("Getting dashboard cases for vendor");

        List<Allocation> allocations = allocationRepository.findByStatusIn(
                List.of("ASSIGNED", "VISITED", "PROMISE_TO_PAY")
        );
        log.info("Found cases: {}", allocations.size());

        return allocations.stream()
                .map(a -> {
                    Map<String, Object> d = a.getAllocationData();

                    return FieldExecutiveDashboardCaseResponse.builder()
                            .caseId("CASE" + a.getId())
                            .loanNumber(a.getLoanNumber())
                            .borrowerName(getString(d, "CUSTOMER NAME"))
                            .loanAmount(getString(d, "POS Amt"))
                            .status(a.getStatus())
                            .phone(getString(d, "phone_1"))
                            .location(getString(d, "LOCATION"))
                            .address(getString(d, "address_priority_1"))
                            .build();
                })
                .toList();
    }

    /**
     * Get my cases (same as FieldExecutive format)
     * Returns detailed case information with all allocation data fields
     */
    public List<FieldExecutiveCaseResponse> getMyCases() {
        log.info("Getting my cases for vendor");

        List<Allocation> allocations = allocationRepository.findByStatusIn(
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

    /**
     * Get complete dashboard statistics for today
     */
    public VendorDashboardResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        log.info("Fetching dashboard stats for date: {}", today);

        List<VisitLog> todayVisits = visitLogRepository.findByVisitDate(today);

        long visitsCompletedToday = todayVisits.stream()
                .filter(v -> v.getDisp() != null)
                .count();

        BigDecimal collectionsToday = todayVisits.stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalActiveCases = allocationRepository.countByStatus("ASSIGNED");

        long assignedCases = totalActiveCases;
        long visitsPendingToday = assignedCases - visitsCompletedToday;
        if (visitsPendingToday < 0) visitsPendingToday = 0;

        List<FieldOfficerDTO> topOfficers = getTopFieldOfficers(5);
        List<AllocationDTO> recentAllocations = getRecentAllocations(10);

        return VendorDashboardResponse.builder()
                .visitsPendingToday(visitsPendingToday)
                .collectionsToday(collectionsToday)
                .visitsCompletedToday(visitsCompletedToday)
                .totalActiveCases(totalActiveCases)
                .topFieldOfficers(topOfficers)
                .recentAllocations(recentAllocations)
                .build();
    }

    /**
     * Get today's visit statistics
     */
    public Map<String, Object> getTodayVisitStats() {
        LocalDate today = LocalDate.now();
        log.info("Fetching today's visit stats");

        List<VisitLog> todayVisits = visitLogRepository.findByVisitDate(today);

        long pending = todayVisits.stream()
                .filter(v -> v.getDisp() == null)
                .count();

        long completed = todayVisits.stream()
                .filter(v -> v.getDisp() != null)
                .count();

        long total = pending + completed;
        double completionRate = total > 0 ? (completed * 100.0) / total : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pending);
        stats.put("completed", completed);
        stats.put("total", total);
        stats.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        return stats;
    }

    /**
     * Get today's collection statistics
     */
    public Map<String, Object> getTodayCollectionStats() {
        LocalDate today = LocalDate.now();
        log.info("Fetching today's collection stats");

        List<VisitLog> todayVisits = visitLogRepository.findByVisitDate(today);

        BigDecimal totalCollected = todayVisits.stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long visitsWithCollection = todayVisits.stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .count();

        BigDecimal averagePerVisit = BigDecimal.ZERO;
        if (visitsWithCollection > 0) {
            averagePerVisit = totalCollected.divide(
                    BigDecimal.valueOf(visitsWithCollection), 2, BigDecimal.ROUND_HALF_UP
            );
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCollected", totalCollected);
        stats.put("visitsWithCollection", visitsWithCollection);
        stats.put("averagePerVisit", averagePerVisit);

        return stats;
    }

    /**
     * Get all field officers with their pending cases
     */
    public List<FieldOfficerDTO> getAllFieldOfficers() {
        log.info("Fetching all field officers with ROLE_FO");

        LocalDate today = LocalDate.now();

        List<UserEntity> fieldOfficers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "ROLE_FO".equals(role.getName())))
                .collect(Collectors.toList());

        log.info("Found {} field officers with ROLE_FO", fieldOfficers.size());

        List<VisitLog> todayVisits = visitLogRepository.findByVisitDate(today);
        Map<Long, List<VisitLog>> todayVisitsByUser = todayVisits.stream()
                .collect(Collectors.groupingBy(VisitLog::getUserId));

        return fieldOfficers.stream()
                .map(officer -> {
                    List<Allocation> userAllocations = allocationRepository.findByFieldExecutiveId(officer.getId());
                    List<VisitLog> userTodayVisits = todayVisitsByUser.getOrDefault(officer.getId(), new ArrayList<>());
                    return buildFieldOfficerDTOWithUserDetails(officer, userAllocations, userTodayVisits);
                })
                .sorted(Comparator.comparingInt(FieldOfficerDTO::getPendingCases).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get top field officers by number of cases
     */
    public List<FieldOfficerDTO> getTopFieldOfficers(int limit) {
        log.info("Fetching top {} field officers", limit);
        return getAllFieldOfficers().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get recent allocations
     */
    public List<AllocationDTO> getRecentAllocations(int limit) {
        log.info("Fetching recent {} ASSIGNED allocations", limit);

        List<Allocation> allocations = allocationRepository.findByStatus("ASSIGNED");
        return allocations.stream()
                .sorted(Comparator.comparing(Allocation::getCreatedAt).reversed())
                .limit(limit)
                .map(this::buildAllocationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get cases for vendor
     */
    public List<AllocationDTO> getCasesForVendor(List<String> statuses) {
        log.info("Fetching cases with statuses: {}", statuses);

        List<String> statusFilter = statuses != null && !statuses.isEmpty()
                ? statuses
                : List.of("ASSIGNED");

        List<Allocation> allocations = allocationRepository.findByStatusIn(statusFilter);
        return allocations.stream()
                .map(this::buildAllocationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get field officer's performance details
     */
    public Map<String, Object> getFieldOfficerPerformance(Long userId) {
        log.info("Fetching performance for officer: {}", userId);

        LocalDate today = LocalDate.now();
        List<VisitLog> userVisits = visitLogRepository.findByUserId(userId);
        List<Allocation> userAllocations = allocationRepository.findByFieldExecutiveId(userId);

        long totalCases = userAllocations.size();
        long assignedCases = userAllocations.stream()
                .filter(a -> "ASSIGNED".equals(a.getStatus()))
                .count();
        long completedCases = userAllocations.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .count();

        long visitedCases = userVisits.stream()
                .filter(v -> v.getDisp() != null)
                .count();

        long pendingCases = totalCases - visitedCases;
        if (pendingCases < 0) pendingCases = 0;

        double completionRate = totalCases > 0 ? (visitedCases * 100.0) / totalCases : 0;

        BigDecimal totalCollection = userVisits.stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long todayVisits = userVisits.stream()
                .filter(v -> v.getVisitDate().equals(today))
                .count();

        long todayCompleted = userVisits.stream()
                .filter(v -> v.getVisitDate().equals(today) && v.getDisp() != null)
                .count();

        Map<String, Object> performance = new HashMap<>();
        performance.put("userId", userId);
        performance.put("totalCases", totalCases);
        performance.put("assignedCases", assignedCases);
        performance.put("pendingCases", pendingCases);
        performance.put("completedCases", completedCases);
        performance.put("visitedCases", visitedCases);
        performance.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        performance.put("totalCollection", totalCollection);
        performance.put("todayVisits", todayVisits);
        performance.put("todayCompleted", todayCompleted);

        return performance;
    }

    /**
     * Get cases assigned to a field officer
     */
    public List<AllocationDTO> getOfficerCases(Long userId, List<String> statuses) {
        log.info("Fetching cases for officer: {} with statuses: {}", userId, statuses);

        List<Allocation> allocations = allocationRepository.findByFieldExecutiveIdAndStatusIn(userId, statuses);
        return allocations.stream()
                .map(this::buildAllocationDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get visit addresses for a loan (same as FieldExecutive)
     */
    public List<String> getVisitAddresses(String loanNumber) {
        if (loanNumber == null || loanNumber.isBlank()) {
            throw new IllegalArgumentException("loanNumber is required");
        }

        Allocation allocation = allocationRepository.findByLoanNumber(loanNumber)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        Map<String, Object> data = allocation.getAllocationData();
        if (data == null || data.isEmpty()) {
            return List.of();
        }

        return data.entrySet().stream()
                .filter(e -> e.getKey().toLowerCase().startsWith("address"))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .map(value -> {
                    if (value instanceof Number num) {
                        long longVal = num.longValue();
                        if (num.doubleValue() == longVal) {
                            return String.valueOf(longVal);
                        }
                        return num.toString();
                    }
                    return value.toString();
                })
                .map(s -> s.replaceFirst("^\\d+\\.\\s*", "").trim())
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.equalsIgnoreCase("0"))
                .filter(s -> !s.equals("-"))
                .distinct()
                .toList();
    }

    // ========== Helper Methods ==========

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

    private FieldOfficerDTO buildFieldOfficerDTOWithUserDetails(
            UserEntity user,
            List<Allocation> userAllocations,
            List<VisitLog> todayVisits) {

        long totalCases = userAllocations.size();
        long completedCount = userAllocations.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .count();
        long assignedCases = userAllocations.stream()
                .filter(a -> "ASSIGNED".equals(a.getStatus()))
                .count();

        long pendingCases = assignedCases - todayVisits.stream()
                .filter(v -> v.getDisp() != null)
                .count();
        if (pendingCases < 0) pendingCases = 0;

        double completionRate = totalCases > 0 ? (completedCount * 100.0) / totalCases : 0;

        BigDecimal collectionAmount = todayVisits.stream()
                .filter(v -> v.getAmount() != null && v.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(VisitLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate latestVisit = todayVisits.stream()
                .map(VisitLog::getVisitDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        return FieldOfficerDTO.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .status(!userAllocations.isEmpty() ? "active" : "inactive")
                .totalCases((int) totalCases)
                .completedCases((int) completedCount)
                .pendingCases((int) pendingCases)
                .date(latestVisit.toString())
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .collectionsAmount(collectionAmount)
                .build();
    }

    private AllocationDTO buildAllocationDTO(Allocation allocation) {
        Map<String, Object> data = allocation.getAllocationData();

        String loanNumber = allocation.getLoanNumber();
        String customerName = data != null ? (String) data.getOrDefault("CUSTOMER NAME", "N/A") : "N/A";
        String borrowerName = data != null ? (String) data.getOrDefault("BORROWER NAME", "N/A") : customerName;
        String location = data != null ? (String) data.getOrDefault("LOCATION", "N/A") : "N/A";
        String phone = data != null ? (String) data.getOrDefault("PHONE", "N/A") : "N/A";
        String address = data != null ? (String) data.getOrDefault("ADDRESS", "N/A") : "N/A";
        String amount = data != null ? String.valueOf(data.getOrDefault("AMOUNT", "N/A")) : "N/A";

        return AllocationDTO.builder()
                .id(allocation.getId())
                .caseId(allocation.getId().toString())
                .loanNumber(loanNumber)
                .borrowerName(borrowerName)
                .customerName(customerName)
                .location(location)
                .phone(phone)
                .address(address)
                .loanAmount(amount)
                .status(allocation.getStatus() != null ? allocation.getStatus() : "UNASSIGNED")
                .fieldExecutiveId(allocation.getFieldExecutiveId())
                .date(allocation.getCreatedAt() != null
                        ? allocation.getCreatedAt().toLocalDate().toString()
                        : LocalDate.now().toString())
                .build();
    }
}
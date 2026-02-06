package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.BankAdminDashboardResponse;
import com.nimis.chatbot.model.entity.VisitLog;
import com.nimis.chatbot.service.BankAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/bank/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BankAdminController {

    private final BankAdminService bankAdminService;

    /**
     * GET /api/bank/dashboard/stats
     * Returns: totalCaseValue, todayCollections, totalCollection,
     *          yesterdayCollection, pendingForApproval
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<BankAdminDashboardResponse> getDashboardStats() {
        log.info("Dashboard stats request");
        BankAdminDashboardResponse stats = bankAdminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/bank/dashboard/collections/pending
     * Returns collections with PENDING_APPROVAL status
     */
    @GetMapping("/collections/pending")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getPendingCollections() {
        log.info("Fetching pending collections");
        List<VisitLog> collections = bankAdminService.getPendingCollections();
        List<Map<String, Object>> response = new ArrayList<>();

        for (VisitLog col : collections) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", col.getId());
            item.put("loanNumber", col.getLoanNumber());
            item.put("allocationId", col.getAllocationId());
            item.put("amount", col.getAmount());
            item.put("collectionStatus", col.getCollectionStatus());
            item.put("createdBy", col.getCreatedBy());
            item.put("visitDate", col.getVisitDate());
            response.add(item);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/bank/dashboard/collections/{id}/approve
     * Approve a collection (PENDING_APPROVAL -> APPROVED)
     */
    @PostMapping("/collections/{id}/approve")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Map<String, Object>> approveCollection(
            @PathVariable Long id,
            Principal principal) {
        log.info("Approving collection: {}", id);
        VisitLog result = bankAdminService.approveCollection(id, principal.getName());

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("success", true);
            response.put("message", "Collection approved");
            response.put("id", result.getId());
        } else {
            response.put("success", false);
            response.put("message", "Collection not found or invalid status");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/bank/dashboard/collections/{id}/reject
     * Reject a collection with reason (PENDING_APPROVAL -> REJECTED)
     */
    @PostMapping("/collections/{id}/reject")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectCollection(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Principal principal) {
        log.info("Rejecting collection: {}", id);
        String reason = request.getOrDefault("reason", "No reason");
        VisitLog result = bankAdminService.rejectCollection(id, principal.getName(), reason);

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("success", true);
            response.put("message", "Collection rejected");
            response.put("id", result.getId());
        } else {
            response.put("success", false);
            response.put("message", "Collection not found");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/bank/dashboard/collections/{id}/mark-deposited
     * Mark collection as deposited (APPROVED -> DEPOSITED)
     */
    @PostMapping("/collections/{id}/mark-deposited")
    @PreAuthorize("hasRole('BANK_ADMIN')")
    public ResponseEntity<Map<String, Object>> markAsDeposited(
            @PathVariable Long id,
            Principal principal) {
        log.info("Marking deposited: {}", id);
        VisitLog result = bankAdminService.markAsDeposited(id, principal.getName());

        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("success", true);
            response.put("message", "Collection marked as deposited");
            response.put("id", result.getId());
        } else {
            response.put("success", false);
            response.put("message", "Collection not found");
        }
        return ResponseEntity.ok(response);
    }
}
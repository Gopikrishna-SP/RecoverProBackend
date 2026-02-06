package com.nimis.chatbot.controller;

import com.nimis.chatbot.service.ManagerAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerAssignmentController {

    private final ManagerAssignmentService managerAssignmentService;

    /**
     * Assign multiple allocations to a user (Field Executive)
     *
     * Endpoint: POST /api/manager/assign?userId={userId}
     * Body: List<Long> allocationIds
     *
     * Example:
     * POST /api/manager/assign?userId=5
     * [1, 2, 3, 4, 5]
     *
     * Sets:
     * - fieldExecutiveId = userId
     * - status = "ASSIGNED"
     * - assignedAt = current timestamp
     */
    @PostMapping("/assign")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> assignCases(
            @RequestParam Long userId,
            @RequestBody List<Long> allocationIds
    ) {
        try {
            managerAssignmentService.assignToUser(userId, allocationIds);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully assigned " + allocationIds.size() + " case(s) to user",
                    "count", allocationIds.size(),
                    "userId", userId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to assign cases: " + e.getMessage()
            ));
        }
    }

    /**
     * Unassign a single allocation from a user
     *
     * Endpoint: POST /api/manager/{allocationId}/unassign
     *
     * Example:
     * POST /api/manager/123/unassign
     *
     * Sets:
     * - fieldExecutiveId = null
     * - status = "UNASSIGNED"
     * - assignedAt = null
     */
    @PostMapping("/{allocationId}/unassign")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> unassignAllocation(
            @PathVariable Long allocationId
    ) {
        try {
            managerAssignmentService.unassignAllocation(allocationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully unassigned allocation",
                    "allocationId", allocationId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to unassign allocation: " + e.getMessage()
            ));
        }
    }

    /**
     * Reassign an allocation to a different user
     *
     * Endpoint: POST /api/manager/{allocationId}/reassign?userId={userId}
     *
     * Example:
     * POST /api/manager/123/reassign?userId=7
     *
     * Sets:
     * - fieldExecutiveId = newUserId
     * - status = "ASSIGNED"
     * - assignedAt = current timestamp (updated)
     */
    @PostMapping("/{allocationId}/reassign")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> reassignAllocation(
            @PathVariable Long allocationId,
            @RequestParam Long userId
    ) {
        try {
            managerAssignmentService.reassignAllocation(allocationId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully reassigned allocation to user",
                    "allocationId", allocationId,
                    "newUserId", userId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to reassign allocation: " + e.getMessage()
            ));
        }
    }

    /**
     * Bulk unassign multiple allocations
     *
     * Endpoint: POST /api/manager/unassign-bulk
     * Body: List<Long> allocationIds
     *
     * Example:
     * POST /api/manager/unassign-bulk
     * [1, 2, 3, 4, 5]
     */
    @PostMapping("/unassign-bulk")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> unassignMultipleAllocations(
            @RequestBody List<Long> allocationIds
    ) {
        try {
            int count = 0;
            for (Long allocationId : allocationIds) {
                managerAssignmentService.unassignAllocation(allocationId);
                count++;
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully unassigned " + count + " allocation(s)",
                    "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to unassign allocations: " + e.getMessage()
            ));
        }
    }

    /**
     * Get assignment summary statistics
     *
     * Endpoint: GET /api/manager/summary
     *
     * Returns: total cases, assigned, unassigned, etc.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('BANK_ADMIN') || hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAssignmentSummary() {
        try {
            Map<String, Object> summary = managerAssignmentService.getAssignmentSummary();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to get summary: " + e.getMessage()
            ));
        }
    }
}
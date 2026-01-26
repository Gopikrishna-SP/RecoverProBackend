package com.nimis.chatbot.controller;

import com.nimis.chatbot.dto.response.FieldExecutiveCaseResponse;
import com.nimis.chatbot.dto.response.FieldExecutiveDashboardCaseResponse;
import com.nimis.chatbot.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/vendor/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class VendorController {

    private final VendorService vendorService;

    /**
     * GET /api/vendor/dashboard/stats
     * Returns all dashboard stats for today
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        try {
            log.info("Fetching dashboard stats for user: {}", authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getDashboardStats());
        } catch (Exception e) {
            log.error("Error fetching dashboard stats", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch dashboard stats"));
        }
    }

    /**
     * GET /api/vendor/dashboard/cases
     * Returns dashboard cases (ASSIGNED, VISITED, PROMISE_TO_PAY)
     */
    @GetMapping("/dashboard/cases")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public List<FieldExecutiveDashboardCaseResponse> getDashboardCases(Authentication authentication) {
        log.info("Getting dashboard cases for vendor: {}", authentication != null ? authentication.getName() : "anonymous");
        return vendorService.getDashboardCases();
    }

    /**
     * GET /api/vendor/dashboard/my-cases
     * Returns detailed case information
     */
    @GetMapping("/my-cases")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public List<FieldExecutiveCaseResponse> getMyCases(Authentication authentication) {
        log.info("Getting my cases for vendor: {}", authentication != null ? authentication.getName() : "anonymous");
        return vendorService.getMyCases();
    }

    /**
     * GET /api/vendor/dashboard/cases/{loanNumber}/addresses
     * Returns visit addresses for a loan
     */
    @GetMapping("/cases/{loanNumber}/addresses")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public List<String> getVisitAddresses(
            @PathVariable String loanNumber,
            Authentication authentication) {
        log.info("Getting visit addresses for loan: {} by user: {}", loanNumber,
                authentication != null ? authentication.getName() : "anonymous");
        return vendorService.getVisitAddresses(loanNumber);
    }

    /**
     * GET /api/vendor/dashboard/visits/today
     * Returns today's visit statistics
     */
    @GetMapping("/visits/today")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTodayVisitStats(Authentication authentication) {
        try {
            log.info("Fetching today's visit stats for user: {}", authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getTodayVisitStats());
        } catch (Exception e) {
            log.error("Error fetching today's visit stats", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch visit stats"));
        }
    }

    /**
     * GET /api/vendor/dashboard/collections/today
     * Returns today's collection statistics
     */
    @GetMapping("/collections/today")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTodayCollectionStats(Authentication authentication) {
        try {
            log.info("Fetching today's collection stats for user: {}", authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getTodayCollectionStats());
        } catch (Exception e) {
            log.error("Error fetching today's collection stats", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch collection stats"));
        }
    }

    /**
     * GET /api/vendor/dashboard/field-officers
     * Returns all field officers with their performance metrics
     */
    @GetMapping("/field-officers")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getAllFieldOfficers(Authentication authentication) {
        try {
            log.info("Fetching all field officers for user: {}", authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getAllFieldOfficers());
        } catch (Exception e) {
            log.error("Error fetching field officers", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch field officers"));
        }
    }

    /**
     * GET /api/vendor/dashboard/field-officers/top/{limit}
     * Returns top field officers by number of cases
     */
    @GetMapping("/field-officers/top/{limit}")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getTopFieldOfficers(
            @PathVariable int limit,
            Authentication authentication) {
        try {
            log.info("Fetching top {} field officers for user: {}", limit, authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getTopFieldOfficers(limit));
        } catch (Exception e) {
            log.error("Error fetching top field officers", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch top field officers"));
        }
    }

    /**
     * GET /api/vendor/dashboard/allocations/recent/{limit}
     * Returns recent allocations with case details
     */
    @GetMapping("/allocations/recent/{limit}")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getRecentAllocations(
            @PathVariable int limit,
            Authentication authentication) {
        try {
            log.info("Fetching recent {} allocations for user: {}", limit, authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getRecentAllocations(limit));
        } catch (Exception e) {
            log.error("Error fetching recent allocations", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch recent allocations"));
        }
    }

    /**
     * GET /api/vendor/dashboard/cases (query param version)
     * Returns all ASSIGNED cases for the vendor
     */
    @GetMapping("/cases")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getCasesForVendor(
            @RequestParam(required = false) List<String> statuses,
            Authentication authentication) {
        try {
            log.info("Fetching ASSIGNED cases for user: {}", authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getCasesForVendor(List.of("ASSIGNED")));
        } catch (Exception e) {
            log.error("Error fetching cases", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch cases"));
        }
    }

    /**
     * GET /api/vendor/dashboard/officer/{userId}/performance
     * Returns performance details for a specific field officer
     */
    @GetMapping("/officer/{userId}/performance")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<Map<String, Object>> getOfficerPerformance(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            log.info("Fetching performance for officer {} by user: {}", userId, authentication != null ? authentication.getName() : "anonymous");
            return ResponseEntity.ok(vendorService.getFieldOfficerPerformance(userId));
        } catch (Exception e) {
            log.error("Error fetching officer performance", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch officer performance"));
        }
    }

    /**
     * GET /api/vendor/dashboard/officer/{userId}/cases
     * Returns all cases assigned to a field officer with optional status filter
     */
    @GetMapping("/officer/{userId}/cases")
    @PreAuthorize("hasRole('VENDOR_ADMIN')")
    public ResponseEntity<?> getOfficerCases(
            @PathVariable Long userId,
            @RequestParam(required = false) List<String> statuses,
            Authentication authentication) {
        try {
            log.info("Fetching cases for officer {} with statuses: {} by user: {}",
                    userId, statuses, authentication != null ? authentication.getName() : "anonymous");

            List<String> statusFilter = statuses != null && !statuses.isEmpty()
                    ? statuses
                    : List.of("UNASSIGNED", "ASSIGNED", "PENDING");

            return ResponseEntity.ok(vendorService.getOfficerCases(userId, statusFilter));
        } catch (Exception e) {
            log.error("Error fetching officer cases", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch officer cases"));
        }
    }
}
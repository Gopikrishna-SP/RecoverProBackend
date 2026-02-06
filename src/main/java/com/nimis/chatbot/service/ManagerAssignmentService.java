package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerAssignmentService {

    private final AllocationRepository allocationRepository;

    /**
     * Assign multiple allocations to a field executive
     *
     * Sets:
     * - fieldExecutiveId = userId
     * - status = "ASSIGNED"
     * - assignedAt = current timestamp
     *
     * @param userId The field executive/officer ID
     * @param allocationIds List of allocation IDs to assign
     */
    @Transactional
    public void assignToUser(Long userId, List<Long> allocationIds) {
        log.info("Assigning {} allocations to user {}", allocationIds.size(), userId);

        List<Allocation> allocations = allocationRepository.findAllById(allocationIds);

        if (allocations.isEmpty()) {
            log.warn("No allocations found for IDs: {}", allocationIds);
            return;
        }

        for (Allocation allocation : allocations) {
            allocation.setFieldExecutiveId(userId);
            allocation.setStatus("ASSIGNED");
            allocation.setAssignedAt(LocalDateTime.now());
            log.debug("Assigned allocation {} to user {}", allocation.getId(), userId);
        }

        allocationRepository.saveAll(allocations);
        log.info("Successfully assigned {} allocations to user {}", allocations.size(), userId);
    }

    /**
     * Unassign a single allocation (remove user assignment)
     *
     * Sets:
     * - fieldExecutiveId = null
     * - status = "UNASSIGNED"
     * - assignedAt = null
     *
     * @param allocationId The allocation ID to unassign
     */
    @Transactional
    public void unassignAllocation(Long allocationId) {
        log.info("Unassigning allocation {}", allocationId);

        allocationRepository.findById(allocationId).ifPresent(allocation -> {
            allocation.setFieldExecutiveId(null);
            allocation.setStatus("UNASSIGNED");
            allocation.setAssignedAt(null);
            allocationRepository.save(allocation);
            log.info("Successfully unassigned allocation {}", allocationId);
        });
    }

    /**
     * Reassign an allocation to a different user
     *
     * Sets:
     * - fieldExecutiveId = newUserId
     * - status = "ASSIGNED"
     * - assignedAt = current timestamp (updated)
     *
     * @param allocationId The allocation ID to reassign
     * @param newUserId The new field executive/officer ID
     */
    @Transactional
    public void reassignAllocation(Long allocationId, Long newUserId) {
        log.info("Reassigning allocation {} to user {}", allocationId, newUserId);

        allocationRepository.findById(allocationId).ifPresent(allocation -> {
            Long oldUserId = allocation.getFieldExecutiveId();
            allocation.setFieldExecutiveId(newUserId);
            allocation.setStatus("ASSIGNED");
            allocation.setAssignedAt(LocalDateTime.now());
            allocationRepository.save(allocation);
            log.info("Successfully reassigned allocation {} from user {} to user {}",
                    allocationId, oldUserId, newUserId);
        });
    }

    /**
     * Bulk unassign multiple allocations
     *
     * @param allocationIds List of allocation IDs to unassign
     */
    @Transactional
    public void unassignBulk(List<Long> allocationIds) {
        log.info("Bulk unassigning {} allocations", allocationIds.size());

        for (Long allocationId : allocationIds) {
            unassignAllocation(allocationId);
        }

        log.info("Completed bulk unassignment of {} allocations", allocationIds.size());
    }

    /**
     * Get assignment summary statistics
     *
     * Returns:
     * - totalAllocations: Total number of allocations
     * - assignedCount: Number of assigned allocations
     * - unassignedCount: Number of unassigned allocations
     * - assignmentPercentage: Percentage of allocations assigned
     *
     * @return Map containing assignment statistics
     */
    public Map<String, Object> getAssignmentSummary() {
        log.info("Fetching assignment summary");

        List<Allocation> allAllocations = allocationRepository.findAll();
        long totalCount = allAllocations.size();
        long assignedCount = allAllocations.stream()
                .filter(a -> a.getFieldExecutiveId() != null)
                .count();
        long unassignedCount = totalCount - assignedCount;
        double assignmentPercentage = totalCount > 0 ? (assignedCount * 100.0) / totalCount : 0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAllocations", totalCount);
        summary.put("assignedCount", assignedCount);
        summary.put("unassignedCount", unassignedCount);
        summary.put("assignmentPercentage", Math.round(assignmentPercentage * 100.0) / 100.0);
        summary.put("timestamp", LocalDateTime.now());

        log.info("Assignment summary - Total: {}, Assigned: {}, Unassigned: {}",
                totalCount, assignedCount, unassignedCount);

        return summary;
    }

    /**
     * Get allocations assigned to a specific user
     *
     * @param userId The field executive/officer ID
     * @return List of allocations assigned to the user
     */
    public List<Allocation> getAllocationsByUser(Long userId) {
        log.info("Fetching allocations for user {}", userId);
        return allocationRepository.findByFieldExecutiveId(userId);
    }

    /**
     * Get all unassigned allocations
     *
     * @return List of unassigned allocations
     */
    public List<Allocation> getUnassignedAllocations() {
        log.info("Fetching unassigned allocations");
        return allocationRepository.findByFieldExecutiveIdIsNull();
    }

    /**
     * Get count of unassigned allocations
     *
     * @return Count of unassigned allocations
     */
    public long getUnassignedCount() {
        return allocationRepository.countByFieldExecutiveIdIsNull();
    }

    /**
     * Get count of allocations assigned to a specific user
     *
     * @param userId The field executive/officer ID
     * @return Count of allocations assigned to the user
     */
    public long getCountByUser(Long userId) {
        return allocationRepository.countByFieldExecutiveId(userId);
    }

    /**
     * Check if an allocation is assigned
     *
     * @param allocationId The allocation ID
     * @return true if allocated, false if unassigned
     */
    public boolean isAllocated(Long allocationId) {
        return allocationRepository.findById(allocationId)
                .map(a -> a.getFieldExecutiveId() != null)
                .orElse(false);
    }

    /**
     * Get user ID of the field executive assigned to an allocation
     *
     * @param allocationId The allocation ID
     * @return User ID of assigned field executive, or null if unassigned
     */
    public Long getAssignedUserId(Long allocationId) {
        return allocationRepository.findById(allocationId)
                .map(Allocation::getFieldExecutiveId)
                .orElse(null);
    }
}
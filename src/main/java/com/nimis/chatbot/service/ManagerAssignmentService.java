package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerAssignmentService {

    private final AllocationRepository allocationRepository;

    @Transactional
    public void assignToUser(Long userId, List<Long> allocationIds) {
        List<Allocation> allocations = allocationRepository.findAllById(allocationIds);

        for (Allocation allocation : allocations) {
            allocation.setFieldExecutiveId(userId);
            allocation.setStatus("ASSIGNED");
            allocation.setAssignedAt(LocalDateTime.now());
        }

        allocationRepository.saveAll(allocations);
    }
}

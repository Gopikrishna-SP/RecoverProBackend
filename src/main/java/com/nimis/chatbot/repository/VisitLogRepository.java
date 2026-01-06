package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    boolean existsByAllocationIdAndVisitDateAndCreatedBy(
            Long allocationId,
            LocalDate visitDate,
            String createdBy
    );

    List<VisitLog> findByAllocationId(Long allocationId);
}

package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    List<VisitLog> findByAllocationId(Long allocationId);

    boolean existsByAllocationIdAndVisitDateAndCreatedBy(
            Long allocationId,
            LocalDate visitDate,
            String createdBy
    );

    List<VisitLog> findByCreatedBy(String createdBy);

    List<VisitLog> findByUserId(Long userId);

    List<VisitLog> findByVisitDateBetween(LocalDate startDate, LocalDate endDate);

    List<VisitLog> findByVisitDate(LocalDate today);
}
package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    Optional<Allocation> findByLoanNumber(String loanNumber);

    List<Allocation> findByFieldExecutiveIdAndStatusIn(
            Long userId,
            List<String> statuses
    );

    @Query(value = """
    SELECT
        a.allocation_data ->> 'LOANNUMBER' AS loanNumber,
        a.allocation_data ->> 'CUSTOMER NAME' AS customerName,
        a.allocation_data ->> 'LOCATION' AS location
    FROM loan_allocation a
    WHERE a.field_executive_id = :userId
    AND a.status IN (:statuses)
    """, nativeQuery = true)
    List<Object[]> findDashboardCases(
            @Param("userId") Long userId,
            @Param("statuses") List<String> statuses
    );

    @Query(value = """
    SELECT
        a.allocation_data ->> 'address_priority_1' AS addr1,
        a.allocation_data ->> 'address_priority_2' AS addr2,
        a.allocation_data ->> 'address_priority_3' AS addr3,
        a.allocation_data ->> 'address_priority_4' AS addr4
    FROM loan_allocation a
    WHERE a.loan_number = :loanNumber
    AND a.field_executive_id = :userId
    """, nativeQuery = true)
    List<Object[]> findVisitAddresses(
            @Param("loanNumber") String loanNumber,
            @Param("userId") Long userId
    );

    long countByStatus(String status);

    List<Allocation> findByFieldExecutiveId(Long userId);

    List<Allocation> findByStatusIn(List<String> statusFilter);

    List<Allocation> findByFieldExecutiveIdIsNotNull();

    List<Allocation> findByStatus(String assigned);
}

package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    Optional<Allocation> findByLoanNumber(String loanNumber);
}

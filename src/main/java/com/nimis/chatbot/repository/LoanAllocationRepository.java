package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.LoanAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanAllocationRepository extends JpaRepository<LoanAllocation, Long> {
}

package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {
}

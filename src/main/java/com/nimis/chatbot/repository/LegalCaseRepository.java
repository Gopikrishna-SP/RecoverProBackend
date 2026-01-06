package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {
    Optional<LegalCase> findByAllocation(Allocation allocation);

}

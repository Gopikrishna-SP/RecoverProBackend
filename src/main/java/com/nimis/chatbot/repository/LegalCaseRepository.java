package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {
}

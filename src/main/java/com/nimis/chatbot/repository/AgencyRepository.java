package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Agency;
import com.nimis.chatbot.model.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByAllocation(Allocation allocation);

}

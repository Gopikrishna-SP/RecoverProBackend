package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Allocation;
import com.nimis.chatbot.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    Optional<Phone> findByAllocation(Allocation allocation);

}

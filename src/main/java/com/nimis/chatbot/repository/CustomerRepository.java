package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAllocation(Allocation allocation);
}

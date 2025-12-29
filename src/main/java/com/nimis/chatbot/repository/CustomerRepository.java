package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

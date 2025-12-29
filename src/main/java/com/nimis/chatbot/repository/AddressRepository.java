package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

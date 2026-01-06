package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Address;
import com.nimis.chatbot.model.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByAllocation(Allocation allocation);

}

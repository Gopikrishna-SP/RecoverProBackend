package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAllocation(Allocation allocation);

}

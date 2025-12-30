package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Allocation;
import com.nimis.chatbot.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByAllocation(Allocation allocation);

}

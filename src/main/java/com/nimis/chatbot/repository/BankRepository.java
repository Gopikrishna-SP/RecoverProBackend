package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    Optional<BankEntity> findByName(String name);
}

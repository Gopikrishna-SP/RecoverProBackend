package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<VendorEntity, Long> {

    Optional<VendorEntity> findByNameAndBankId(String name, Long bankId);

    Optional<VendorEntity> findByIdAndBankId(Long id, Long bankId);

    List<VendorEntity> findAllByBankId(Long bankId);
}

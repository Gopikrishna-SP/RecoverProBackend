package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
}

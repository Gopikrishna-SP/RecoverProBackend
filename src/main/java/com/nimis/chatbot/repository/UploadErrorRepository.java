package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.UploadError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadErrorRepository extends JpaRepository<UploadError, Long> {
}

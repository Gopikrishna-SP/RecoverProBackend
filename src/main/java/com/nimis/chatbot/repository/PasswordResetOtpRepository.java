package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.PasswordResetOtpEntity;
import com.nimis.chatbot.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtpEntity, Long> {

    Optional<PasswordResetOtpEntity> findByUserAndOtpAndVerifiedFalse(UserEntity user, String otp);

    List<PasswordResetOtpEntity> findByUserAndVerifiedFalse(UserEntity user);

    @Query("SELECT COUNT(p) FROM PasswordResetOtpEntity p WHERE p.user.id = :userId AND p.verified = true")
    long countVerifiedOtpByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM PasswordResetOtpEntity p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Login by email
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByIdAndBank_Id(Long id, Long bankId);

    List<UserEntity> findAllByBank_Id(Long bankId);

    List<UserEntity> findAllByVendor_IdAndBank_Id(Long vendorId, Long bankId);

    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

}

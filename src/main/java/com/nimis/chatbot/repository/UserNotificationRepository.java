package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.UserNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository
        extends JpaRepository<UserNotificationEntity, Long> {

    @Query("""
        select un
        from UserNotificationEntity un
        join fetch un.notification n
        where un.user.id = :userId
        order by n.createdAt desc
    """)
    List<UserNotificationEntity> findWithNotificationByUserId(Long userId);

    Optional<UserNotificationEntity>
    findByUserIdAndNotificationId(Long userId, Long notificationId);

    long countByUserIdAndReadFalse(Long userId);
}




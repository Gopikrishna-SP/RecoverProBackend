package com.nimis.chatbot.repository;

import com.nimis.chatbot.model.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    // ==================== EXISTING METHODS ====================
    List<VisitLog> findByAllocationId(Long allocationId);
    List<VisitLog> findByUserId(Long userId);
    List<VisitLog> findByVisitDate(LocalDate visitDate);
    List<VisitLog> findByVisitDateBetween(LocalDate startDate, LocalDate endDate);

    // ==================== COLLECTION APPROVAL METHODS ====================
    /**
     * Find all visit logs with specific collection status
     * Example: findByCollectionStatus("PENDING_APPROVAL")
     */
    List<VisitLog> findByCollectionStatus(String collectionStatus);

    /**
     * Find all visit logs with any of the given collection statuses
     * Example: findByCollectionStatusIn(List.of("APPROVED", "DEPOSITED"))
     */
    List<VisitLog> findByCollectionStatusIn(List<String> statuses);

    /**
     * Find pending collections (amount > 0 and specific statuses)
     * Used for filtering collections that have amounts and need processing
     */
    List<VisitLog> findByAmountIsNotNullAndCollectionStatusIn(List<String> statuses);

    /**
     * Find visit logs for a specific user with specific collection statuses
     */
    List<VisitLog> findByUserIdAndCollectionStatusIn(Long userId, List<String> statuses);

    /**
     * Find visit logs for specific allocation with specific collection statuses
     */
    List<VisitLog> findByAllocationIdAndCollectionStatusIn(Long allocationId, List<String> statuses);

    // ==================== GPS LOCATION METHODS ====================
    /**
     * Find visit logs with valid GPS coordinates
     */
    @Query("SELECT v FROM VisitLog v WHERE v.latitude IS NOT NULL AND v.longitude IS NOT NULL")
    List<VisitLog> findVisitsWithGPSCoordinates();

    /**
     * Find visits within a geographic radius (simplified - use in application layer for accurate distance)
     */
    @Query("SELECT v FROM VisitLog v WHERE v.latitude IS NOT NULL AND v.longitude IS NOT NULL")
    List<VisitLog> findVisitsWithinRadius();

    /**
     * Find visits with GPS data within a date range
     */
    @Query("SELECT v FROM VisitLog v WHERE v.gpsCapturedAt BETWEEN :startTime AND :endTime AND v.latitude IS NOT NULL")
    List<VisitLog> findVisitsWithGPSBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Find visits with GPS accuracy within threshold
     */
    @Query("SELECT v FROM VisitLog v WHERE v.gpsAccuracy <= :maxAccuracy AND v.gpsAccuracy IS NOT NULL")
    List<VisitLog> findVisitsWithHighAccuracy(@Param("maxAccuracy") Double maxAccuracy);

    /**
     * Find visits with potential geofence violations (distance from expected location > threshold)
     */
    @Query("SELECT v FROM VisitLog v WHERE v.distanceFromExpectedLocation > :threshold AND v.distanceFromExpectedLocation IS NOT NULL")
    List<VisitLog> findGeofenceViolations(@Param("threshold") Double threshold);

    /**
     * Find visits with GPS data for a specific user
     */
    @Query("SELECT v FROM VisitLog v WHERE v.userId = :userId AND v.latitude IS NOT NULL AND v.longitude IS NOT NULL ORDER BY v.gpsCapturedAt DESC")
    List<VisitLog> findUserVisitsWithGPS(@Param("userId") Long userId);

    /**
     * Find visits with complete data (image + GPS)
     */
    @Query("SELECT v FROM VisitLog v WHERE v.visitImagePath IS NOT NULL AND v.latitude IS NOT NULL AND v.longitude IS NOT NULL")
    List<VisitLog> findVisitsWithCompleteData();

    /**
     * Find visits without GPS data (for audit/compliance)
     */
    @Query("SELECT v FROM VisitLog v WHERE v.latitude IS NULL OR v.longitude IS NULL")
    List<VisitLog> findVisitsWithoutGPS();

    /**
     * Find visits submitted from outside expected location
     */
    @Query("SELECT v FROM VisitLog v WHERE v.visitStatus = 'SUBMITTED' AND v.distanceFromExpectedLocation > :threshold")
    List<VisitLog> findAnomalousVisits(@Param("threshold") Double threshold);

    // ==================== VISIT STATUS METHODS ====================
    /**
     * Find visits by status
     */
    List<VisitLog> findByVisitStatus(String visitStatus);

    /**
     * Find visits submitted within date range
     */
    @Query("SELECT v FROM VisitLog v WHERE v.submittedAt BETWEEN :startTime AND :endTime ORDER BY v.submittedAt DESC")
    List<VisitLog> findSubmittedVisitsBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Find all pending collections with GPS data
     */
    @Query("SELECT v FROM VisitLog v WHERE v.collectionStatus IN (:statuses) AND v.latitude IS NOT NULL AND v.longitude IS NOT NULL")
    List<VisitLog> findPendingCollectionsWithGPS(@Param("statuses") List<String> statuses);
}
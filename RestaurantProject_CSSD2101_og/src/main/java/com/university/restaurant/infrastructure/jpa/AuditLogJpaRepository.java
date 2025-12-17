package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository for AuditLogEntity.
 * Provides CRUD operations and custom queries for audit logs.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    /**
     * Find audit logs by user ID.
     */
    List<AuditLogEntity> findByUserId(String userId);

    /**
     * Find audit logs by action type.
     */
    List<AuditLogEntity> findByAction(String action);

    /**
     * Find audit logs by entity type.
     */
    List<AuditLogEntity> findByEntityType(String entityType);

    /**
     * Find audit logs for a specific entity.
     */
    List<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, String entityId);

    /**
     * Find audit logs by role (Manager, Waiter, Chef).
     */
    List<AuditLogEntity> findByRole(String role);

    /**
     * Find audit logs between two dates.
     */
    List<AuditLogEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Get all audit logs ordered by timestamp (newest first).
     */
    List<AuditLogEntity> findAllByOrderByTimestampDesc();

    /**
     * Get the most recent audit log (for hash chain verification).
     */
    @Query("SELECT a FROM AuditLogEntity a ORDER BY a.id DESC LIMIT 1")
    AuditLogEntity findMostRecent();

    /**
     * Verify audit log chain integrity.
     */
    @Query("SELECT COUNT(a) FROM AuditLogEntity a")
    long countAll();
}

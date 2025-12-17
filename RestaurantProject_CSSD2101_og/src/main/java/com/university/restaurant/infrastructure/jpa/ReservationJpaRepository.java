package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.ReservationEntity;
import com.university.restaurant.model.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for ReservationEntity.
 * Provides CRUD operations and custom queries for reservations.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, UUID> {

    /**
     * Find reservations by status.
     */
    List<ReservationEntity> findByStatus(ReservationStatus status);

    /**
     * Find active reservations (CONFIRMED or SEATED).
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.status IN ('CONFIRMED', 'SEATED')")
    List<ReservationEntity> findActiveReservations();

    /**
     * Find reservations on a specific date.
     */
    @Query("SELECT r FROM ReservationEntity r WHERE DATE(r.reservationTime) = :date")
    List<ReservationEntity> findByDate(LocalDate date);

    /**
     * Find reservations between two date/times.
     */
    List<ReservationEntity> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find reservations by assigned table number.
     */
    List<ReservationEntity> findByAssignedTable(Integer tableNumber);

    /**
     * Find reservations by customer phone number.
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.customer.phone = :phone")
    List<ReservationEntity> findByCustomerPhone(String phone);

    /**
     * Find reservations by customer name (case-insensitive).
     */
    @Query("SELECT r FROM ReservationEntity r WHERE LOWER(r.customer.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ReservationEntity> searchByCustomerName(String name);

    /**
     * Count reservations by date and status.
     */
    @Query("SELECT COUNT(r) FROM ReservationEntity r WHERE DATE(r.reservationTime) = :date AND r.status = :status")
    long countByDateAndStatus(LocalDate date, ReservationStatus status);
}

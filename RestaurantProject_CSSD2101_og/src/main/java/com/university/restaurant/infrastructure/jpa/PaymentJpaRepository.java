package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.PaymentEntity;
import com.university.restaurant.model.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for PaymentEntity.
 * Provides CRUD operations and custom queries for payments.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, String> {

    /**
     * Find payment by order ID.
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.order.id = :orderId")
    Optional<PaymentEntity> findByOrderId(UUID orderId);

    /**
     * Find payments by payment method.
     */
    List<PaymentEntity> findByMethod(PaymentMethod method);

    /**
     * Find payments on a specific date.
     */
    @Query("SELECT p FROM PaymentEntity p WHERE DATE(p.timestamp) = :date")
    List<PaymentEntity> findByDate(LocalDate date);

    /**
     * Find payments between two dates.
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.timestamp BETWEEN :startDate AND :endDate")
    List<PaymentEntity> findBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get total payment amount for a specific date.
     */
    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE DATE(p.timestamp) = :date")
    Double getTotalAmountForDate(LocalDate date);

    /**
     * Get total payment amount by payment method.
     */
    @Query("SELECT SUM(p.amount) FROM PaymentEntity p WHERE p.method = :method")
    Double getTotalAmountByMethod(PaymentMethod method);
}

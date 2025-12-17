package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.OrderEntity;
import com.university.restaurant.model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for OrderEntity.
 * Provides CRUD operations and custom queries for orders.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * Find orders by status.
     */
    List<OrderEntity> findByStatus(OrderStatus status);

    /**
     * Find orders by table number.
     */
    List<OrderEntity> findByTableNumber(Integer tableNumber);

    /**
     * Find orders by assigned waiter.
     */
    List<OrderEntity> findByAssignedWaiterId(String waiterId);

    /**
     * Find orders created after a specific date/time.
     */
    List<OrderEntity> findByCreatedAtAfter(OffsetDateTime dateTime);

    /**
     * Find orders by status and table number.
     */
    List<OrderEntity> findByStatusAndTableNumber(OrderStatus status, Integer tableNumber);

    /**
     * Get total revenue for paid orders (sum of total amounts).
     */
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.status = 'PAID'")
    Double getTotalRevenue();

    /**
     * Get total revenue for paid orders created after a specific date.
     */
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.status = 'PAID' AND o.createdAt >= :startDate")
    Double getTotalRevenueSince(OffsetDateTime startDate);

    /**
     * Count orders by status.
     */
    long countByStatus(OrderStatus status);
}
package com.university.restaurant.infrastructure.adapter;

import com.university.restaurant.infrastructure.adapter.mapper.OrderMapper;
import com.university.restaurant.infrastructure.jpa.OrderJpaRepository;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Adapter implementing OrderRepository port.
 * Bridges Alice's OrderRepository interface with Spring Data JPA.
 *
 * Note: This implementation has limitations due to domain model constraints.
 * The Order domain class doesn't support reconstruction from persistence,
 * so we can only save Orders, not fully retrieve them as domain objects.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class OrderJpaAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepo;
    private final OrderMapper mapper;

    public OrderJpaAdapter(OrderJpaRepository jpaRepo, OrderMapper mapper) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        // Note: Cannot fully convert entity back to domain Order
        // This is a design limitation that would need to be addressed
        // by modifying the domain Order class
        throw new UnsupportedOperationException(
                "Order retrieval not supported due to domain model constraints. " +
                        "Consider using a DTO approach or modifying Order domain class."
        );
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        // Same limitation as findById
        throw new UnsupportedOperationException(
                "Order retrieval not supported due to domain model constraints."
        );
    }

    @Override
    public List<Order> findByTable(int tableNumber) {
        // Same limitation
        throw new UnsupportedOperationException(
                "Order retrieval not supported due to domain model constraints."
        );
    }

    @Override
    public void save(Order order) {
        // Saving works fine - we can convert domain to entity
        jpaRepo.save(mapper.toEntity(order));
    }
}
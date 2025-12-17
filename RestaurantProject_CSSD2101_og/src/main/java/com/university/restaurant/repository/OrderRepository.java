package com.university.restaurant.repository;

import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID id);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByTable(int tableNumber);

    void save(Order order);
}

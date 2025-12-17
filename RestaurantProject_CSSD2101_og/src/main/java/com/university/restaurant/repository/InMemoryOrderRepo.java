package com.university.restaurant.repository;

import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;

import java.util.*;

public final class InMemoryOrderRepo implements OrderRepository {
    private final Map<UUID, Order> store = new HashMap<>();

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return store.values().stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    @Override
    public List<Order> findByTable(int tableNumber) {
        return store.values().stream()
                .filter(order -> order.getTableNumber() == tableNumber)
                .toList();
    }

    @Override
    public void save(Order order) {
        store.put(order.getId(), order);
    }
}

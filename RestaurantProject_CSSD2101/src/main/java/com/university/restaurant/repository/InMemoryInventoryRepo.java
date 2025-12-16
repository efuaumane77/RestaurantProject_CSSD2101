package com.university.restaurant.repository;

import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.inventory.StockStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryInventoryRepo implements InventoryRepository {
    private final Map<String, InventoryItem> store = new HashMap<>();

    @Override
    public Optional<InventoryItem> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<InventoryItem> findByName(String name) {
        return store.values().stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<InventoryItem> findByStatus(StockStatus status) {
        return store.values().stream()
                .filter(item -> item.getStatus() == status)
                .toList();
    }

    @Override
    public void save(InventoryItem item) {
        store.put(item.getId(), item);
    }
}

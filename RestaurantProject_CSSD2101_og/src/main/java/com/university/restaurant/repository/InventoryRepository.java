package com.university.restaurant.repository;

import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.inventory.StockStatus;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    Optional<InventoryItem> findById(String id);

    Optional<InventoryItem> findByName(String name);

    List<InventoryItem> findByStatus(StockStatus status);

    void save(InventoryItem item);
}

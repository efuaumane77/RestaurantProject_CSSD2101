package com.university.restaurant.infrastructure.jpa;

import com.university.restaurant.infrastructure.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for InventoryEntity.
 * Provides CRUD operations and custom queries for inventory items.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, String> {

    /**
     * Find inventory item by name (case-insensitive).
     */
    Optional<InventoryEntity> findByNameIgnoreCase(String name);

    /**
     * Find items with low stock (stock level <= reorder threshold).
     */
    @Query("SELECT i FROM InventoryEntity i WHERE i.stockLevel <= i.reorderThreshold")
    List<InventoryEntity> findLowStockItems();

    /**
     * Find items that are out of stock (stock level = 0).
     */
    @Query("SELECT i FROM InventoryEntity i WHERE i.stockLevel = 0")
    List<InventoryEntity> findOutOfStockItems();

    /**
     * Find items with stock above a certain level.
     */
    @Query("SELECT i FROM InventoryEntity i WHERE i.stockLevel > :minLevel")
    List<InventoryEntity> findItemsWithStockAbove(Integer minLevel);

    /**
     * Find all items ordered by stock level (ascending - lowest first).
     */
    List<InventoryEntity> findAllByOrderByStockLevelAsc();

    /**
     * Count items with low stock.
     */
    @Query("SELECT COUNT(i) FROM InventoryEntity i WHERE i.stockLevel <= i.reorderThreshold")
    long countLowStockItems();

    /**
     * Count items that are out of stock.
     */
    @Query("SELECT COUNT(i) FROM InventoryEntity i WHERE i.stockLevel = 0")
    long countOutOfStockItems();
}
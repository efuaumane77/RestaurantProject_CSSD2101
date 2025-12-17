package com.university.restaurant.infrastructure.entity;

import com.university.restaurant.model.inventory.StockStatus;
import jakarta.persistence.*;

/**
 * JPA Entity for Inventory items.
 * Tracks stock levels for ingredients.
 *
 * @author Mahdis (Infrastructure Layer)
 */
@Entity
@Table(name = "inventory")
public class InventoryEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "stock_level", nullable = false)
    private Integer stockLevel;

    @Column(name = "reorder_threshold", nullable = false)
    private Integer reorderThreshold;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    // We don't store StockStatus in DB - it's computed from stockLevel
    @Transient
    private StockStatus status;

    // Default constructor for JPA
    public InventoryEntity() {}

    // Constructor
    public InventoryEntity(String id, String name, String unit, Integer stockLevel,
                           Integer reorderThreshold, Integer maxCapacity) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.maxCapacity = maxCapacity;
    }

    // Compute status based on stock level
    public StockStatus getStatus() {
        if (stockLevel == 0) return StockStatus.OUT_OF_STOCK;
        if (stockLevel <= reorderThreshold) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(Integer stockLevel) {
        this.stockLevel = stockLevel;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}

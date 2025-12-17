package com.university.restaurant.infrastructure.dto;

import com.university.restaurant.model.inventory.StockStatus;

/**
 * DTO for Inventory item responses.
 *
 * @author Mahdis (Infrastructure Layer)
 */
public class InventoryDTO {
    private String id;
    private String name;
    private String unit;
    private Integer stockLevel;
    private Integer reorderThreshold;
    private Integer maxCapacity;
    private StockStatus status;

    // Constructors
    public InventoryDTO() {}

    public InventoryDTO(String id, String name, String unit, Integer stockLevel,
                        Integer reorderThreshold, Integer maxCapacity, StockStatus status) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.maxCapacity = maxCapacity;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getStockLevel() { return stockLevel; }
    public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }

    public Integer getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public StockStatus getStatus() { return status; }
    public void setStatus(StockStatus status) { this.status = status; }
}
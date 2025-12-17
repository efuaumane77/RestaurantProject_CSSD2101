package com.university.restaurant.model.inventory;

/**
 * @author Efua Itoadon-Umane
 * Represents an inventory record for a single ingredient used by the restaurant.
 *
 * This class models the current stock state of an ingredient, including its
 * quantity, unit of measurement, and restocking constraints. It encapsulates
 * inventory-related rules such as stock consumption, restocking limits, and
 * stock status evaluation.
 *
 * Responsibilities are:
 * - Track current stock levels for an ingredient
 * - Determine stock status (in stock, low stock, out of stock)
 * - Enforce inventory constraints such as maximum capacity
 *
 */

public final class InventoryItem {
    private final String id, name;
    private final String unit;
    private int stockLevel;
    private final int reorderThreshold;
    private final int maxCapacity;

    /**
     * Creates a new item that will be tracked in the inventory
     * @param id for the ingredient
     * @param name
     * @param unit
     * @param stockLevel how many are left
     * @param reorderThreshold predictive reorder before stock levels reach 0
     * @param maxCapacity
     */

    public InventoryItem(String id, String name, String unit, int stockLevel, int reorderThreshold,
                         int maxCapacity){
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.maxCapacity = maxCapacity;
    }

    public StockStatus getStatus(){
        if (stockLevel == 0) return StockStatus.OUT_OF_STOCK;
        if (stockLevel <= reorderThreshold) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    public void consume(int quantity) {
        if (quantity > stockLevel)
            throw new IllegalStateException("Insufficient stock: " + name);
        stockLevel -= quantity;
    }

    public void restock(int quantity) {
        stockLevel = Math.min(stockLevel + quantity, maxCapacity);
    }

    @Override
    public String toString() {
        return "InventoryItem[%s: %s | %d %s | Status=%s]"
                .formatted(id, name, stockLevel, unit, getStatus());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getStockLevel() { return stockLevel; }

    public String getUnit() { return unit; }
    public int getReorderThreshold() { return reorderThreshold; }
    public int getMaxCapacity() { return maxCapacity; }
}

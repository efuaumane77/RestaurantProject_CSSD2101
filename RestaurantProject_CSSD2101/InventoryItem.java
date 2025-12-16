package com.university.restaurant.model.inventory;

public final class InventoryItem {
    private final String id, name;
    private final String unit;
    private int stockLevel;
    private final int reorderThreshold;
    private final int maxCapacity;

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
}

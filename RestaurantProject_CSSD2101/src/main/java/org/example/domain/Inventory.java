package org.example.domain;
import java.awt.*;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

enum StockStatus {IN_STOCK, LOW_STOCK, OUT_OF_STOCK}

final class InventoryItem {
    private final String id, name;
    private final String unit;
    private int stockLevel;
    private final int reorderThreshold;
    private final int maxCapacity;

    InventoryItem(String id, String name, String unit, int stockLevel, int reorderThreshold,
              int maxCapacity){
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockLevel = stockLevel;
        this.reorderThreshold = reorderThreshold;
        this.maxCapacity = maxCapacity;
    }

    StockStatus getStatus(){
        if (stockLevel == 0) return StockStatus.OUT_OF_STOCK;
        if (stockLevel <= reorderThreshold) return StockStatus.LOW_STOCK;
        return StockStatus.IN_STOCK;
    }

    void consume(int quantity) {
        if (quantity > stockLevel)
            throw new IllegalStateException("Insufficient stock: " + name);
        stockLevel -= quantity;
    }

    void restock(int quantity) {
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

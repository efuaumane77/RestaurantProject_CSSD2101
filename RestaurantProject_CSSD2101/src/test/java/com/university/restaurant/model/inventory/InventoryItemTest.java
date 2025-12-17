//package com.university.restaurant.model.inventory;

//public class InventoryItemTest {
//}

// ==================== InventoryItemTest.java ====================
package com.university.restaurant.model.inventory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    @Test
    void testConstructorAndGetters() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 50, 10, 100);

        assertEquals("I001", item.getId());
        assertEquals("Tomato", item.getName());
        assertEquals(50, item.getStockLevel());
    }

    @Test
    void testGetStatusInStock() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 50, 10, 100);
        assertEquals(StockStatus.IN_STOCK, item.getStatus());
    }

    @Test
    void testGetStatusLowStock() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 10, 10, 100);
        assertEquals(StockStatus.LOW_STOCK, item.getStatus());
    }

    @Test
    void testGetStatusOutOfStock() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 0, 10, 100);
        assertEquals(StockStatus.OUT_OF_STOCK, item.getStatus());
    }

    @Test
    void testConsumeSuccess() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 50, 10, 100);
        item.consume(20);
        assertEquals(30, item.getStockLevel());
    }

    @Test
    void testConsumeInsufficientStock() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 10, 5, 100);

        assertThrows(IllegalStateException.class, () -> item.consume(20));
    }

    @Test
    void testRestockWithinCapacity() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 50, 10, 100);
        item.restock(30);
        assertEquals(80, item.getStockLevel());
    }

    @Test
    void testRestockExceedsCapacity() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 90, 10, 100);
        item.restock(30);
        assertEquals(100, item.getStockLevel()); // Capped at maxCapacity
    }

    @Test
    void testToString() {
        InventoryItem item = new InventoryItem("I001", "Tomato", "kg", 50, 10, 100);
        String result = item.toString();

        assertTrue(result.contains("I001"));
        assertTrue(result.contains("Tomato"));
        assertTrue(result.contains("50"));
        assertTrue(result.contains("IN_STOCK"));
    }
}
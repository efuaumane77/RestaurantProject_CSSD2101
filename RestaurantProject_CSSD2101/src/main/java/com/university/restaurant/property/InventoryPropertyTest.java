package com.university.restaurant.property;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.inventory.StockStatus;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Property-based tests for InventoryItem using QuickCheck.
 */
@RunWith(JUnitQuickcheck.class)
public class InventoryPropertyTest {

    /**
     * Property: Stock level should never go negative.
     */
    @Property
    public void stockLevelShouldNeverBeNegative(int initialStock, int consumed) {
        // Arrange
        int positiveStock = Math.abs(initialStock % 1000) + 1;
        InventoryItem item = new InventoryItem(
            "item-1", 
            "Test Item", 
            "kg", 
            positiveStock, 
            10, 
            1000
        );

        // Act & Assert
        if (consumed <= positiveStock) {
            item.consume(consumed);
            assertTrue("Stock level should be non-negative", item.getStockLevel() >= 0);
        } else {
            assertThrows(
                "Should throw when consuming more than available",
                IllegalStateException.class,
                () -> item.consume(consumed)
            );
        }
    }

    /**
     * Property: Restocking should respect max capacity.
     */
    @Property
    public void restockShouldRespectMaxCapacity(int initialStock, int restockAmount) {
        // Arrange
        int maxCapacity = 1000;
        int safeInitialStock = Math.abs(initialStock % maxCapacity);
        int safeRestockAmount = Math.abs(restockAmount % (maxCapacity * 2));
        
        InventoryItem item = new InventoryItem(
            "item-1", 
            "Test Item", 
            "kg", 
            safeInitialStock, 
            10, 
            maxCapacity
        );

        // Act
        item.restock(safeRestockAmount);

        // Assert
        assertTrue("Stock should not exceed max capacity", 
                   item.getStockLevel() <= maxCapacity);
    }

    /**
     * Property: Status should correctly reflect stock level.
     */
    @Property
    public void statusShouldReflectStockLevel(int stockLevel, int reorderThreshold) {
        // Arrange
        int safeStock = Math.abs(stockLevel % 1000);
        int safeThreshold = Math.abs(reorderThreshold % 100) + 1;
        
        InventoryItem item = new InventoryItem(
            "item-1", 
            "Test Item", 
            "kg", 
            safeStock, 
            safeThreshold, 
            1000
        );

        // Assert
        if (safeStock == 0) {
            assertEquals(StockStatus.OUT_OF_STOCK, item.getStatus());
        } else if (safeStock <= safeThreshold) {
            assertEquals(StockStatus.LOW_STOCK, item.getStatus());
        } else {
            assertEquals(StockStatus.IN_STOCK, item.getStatus());
        }
    }

    /**
     * Property: Consume and restock should be inverse operations.
     */
    @Property
    public void consumeAndRestockShouldBeInverse(int amount) {
        // Arrange
        int safeAmount = Math.abs(amount % 100) + 1;
        InventoryItem item = new InventoryItem(
            "item-1", 
            "Test Item", 
            "kg", 
            500, 
            10, 
            1000
        );
        int originalStock = item.getStockLevel();

        // Act
        item.consume(safeAmount);
        item.restock(safeAmount);

        // Assert
        assertEquals("Stock should return to original level", 
                    originalStock, item.getStockLevel());
    }
}

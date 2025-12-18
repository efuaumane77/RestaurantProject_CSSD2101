package com.university.restaurant.service;

import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.inventory.StockStatus;
import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.repository.InventoryRepository;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private MenuRepository menuRepo;

    @Mock
    private RestaurantAuditLogRepository auditRepo;

    private InventoryService service;
    private Manager manager;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        service = new InventoryService(inventoryRepo, menuRepo, auditRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
        when(auditRepo.tailHash()).thenReturn("GENESIS");
    }

    @Test
    void reduceStock_withManagerRole_shouldSucceed() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 100, 10, 500);
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));

        service.reduceStock(manager, "item-1", 20);

        assertEquals(80, item.getStockLevel());
        verify(inventoryRepo).save(item);
        verify(auditRepo).append(any());
    }

    @Test
    void reduceStock_withWaiterRole_shouldThrowSecurityException() {
        assertThrows(SecurityException.class, () -> {
            service.reduceStock(waiter, "item-1", 10);
        });
        verify(inventoryRepo, never()).save(any());
    }

    @Test
    void reduceStock_insufficientStock_shouldThrowException() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 5, 10, 500);
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));

        assertThrows(IllegalStateException.class, () -> {
            service.reduceStock(manager, "item-1", 10);
        });
    }

    @Test
    void reduceStock_toZero_shouldMarkMenuItemUnavailable() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 10, 10, 500);
        MenuItem menuItem = new Drink("item-1", "Tomato Juice", "desc", 5.99, false);
        
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));
        when(menuRepo.findById("item-1")).thenReturn(Optional.of(menuItem));

        service.reduceStock(manager, "item-1", 10);

        assertEquals(0, item.getStockLevel());
        assertFalse(menuItem.isAvailable());
        verify(menuRepo).save(menuItem);
    }

    @Test
    void increaseStock_shouldAddStock() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 50, 10, 500);
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));

        service.increaseStock(manager, "item-1", 30);

        assertEquals(80, item.getStockLevel());
        verify(inventoryRepo).save(item);
    }

    @Test
    void increaseStock_exceedingCapacity_shouldCapAtMax() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 450, 10, 500);
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));

        service.increaseStock(manager, "item-1", 100);

        assertEquals(500, item.getStockLevel());
        verify(inventoryRepo).save(item);
    }

    @Test
    void increaseStock_fromZero_shouldMarkMenuItemAvailable() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 0, 10, 500);
        MenuItem menuItem = new Drink("item-1", "Tomato Juice", "desc", 5.99, false);
        menuItem.setAvailable(false);
        
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));
        when(menuRepo.findById("item-1")).thenReturn(Optional.of(menuItem));

        service.increaseStock(manager, "item-1", 20);

        assertEquals(20, item.getStockLevel());
        assertTrue(menuItem.isAvailable());
        verify(menuRepo).save(menuItem);
    }

    @Test
    void getStockLevel_shouldReturnCorrectValue() {
        InventoryItem item = new InventoryItem("item-1", "Tomatoes", "kg", 75, 10, 500);
        when(inventoryRepo.findById("item-1")).thenReturn(Optional.of(item));

        int stockLevel = service.getStockLevel("item-1");

        assertEquals(75, stockLevel);
    }

    @Test
    void getStockLevel_nonExistentItem_shouldThrowException() {
        when(inventoryRepo.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.getStockLevel("invalid");
        });
    }
}

package com.university.restaurant.service;

import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.staff.*;
import com.university.restaurant.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    InMemoryInventoryRepo invRepo;
    InMemoryMenuRepo menuRepo;
    InMemoryRestaurantAuditRepo audits;
    InventoryService service;

    // UPDATED ROLES
    Manager alice = new Manager("m1", "Alice Manager");
    Waiter bob = new Waiter("w1", "Bob Waiter");
    Chef chef = new Chef("c1", "Charlie Chef");

    InventoryItem flour;
    MenuItem entree;

    @BeforeEach
    void setup() {
        invRepo = new InMemoryInventoryRepo();
        menuRepo = new InMemoryMenuRepo();
        audits = new InMemoryRestaurantAuditRepo();

        service = new InventoryService(invRepo, menuRepo, audits);

        // REAL MenuItem
        entree = new Entree(
                "item1",
                "Pasta",
                "Delicious pasta",
                12.00,
                DietaryType.REGULAR,
                List.of("flour", "sauce"),
                10
        );
        menuRepo.save(entree);

        // Inventory entry
        flour = new InventoryItem("item1", "Pasta", "units", 10, 2, 20);
        invRepo.save(flour);
    }

    // -------------------------------------------------------
    // REDUCE STOCK
    // -------------------------------------------------------

    @Test
    void managerAliceCanReduceStock() {
        service.reduceStock(alice, "item1", 5);

        InventoryItem updated = invRepo.findById("item1").orElseThrow();
        assertEquals(5, updated.getStockLevel());
        assertEquals(1, audits.all().size());
    }

    @Test
    void reducingStockToZeroMarksMenuItemUnavailable() {
        service.reduceStock(alice, "item1", 10);

        MenuItem m = menuRepo.findById("item1").orElseThrow();
        assertFalse(m.isAvailable());
    }

    @Test
    void waiterBobCannotReduceStock() {
        assertThrows(SecurityException.class,
                () -> service.reduceStock(bob, "item1", 5));
    }

    @Test
    void chefCannotReduceStock() {
        assertThrows(SecurityException.class,
                () -> service.reduceStock(chef, "item1", 3));
    }

    @Test
    void reduceStock_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.reduceStock(alice, "WRONG", 5));
    }

    // -------------------------------------------------------
    // INCREASE STOCK
    // -------------------------------------------------------

    @Test
    void managerAliceCanIncreaseStock() {
        service.increaseStock(alice, "item1", 3);

        InventoryItem updated = invRepo.findById("item1").orElseThrow();
        assertEquals(13, updated.getStockLevel());
        assertEquals(1, audits.all().size());
    }

    @Test
    void increasingStockFromZeroMarksMenuItemAvailable() {
        service.reduceStock(alice, "item1", 10); // stock = 0
        service.increaseStock(alice, "item1", 5);

        MenuItem m = menuRepo.findById("item1").orElseThrow();
        assertTrue(m.isAvailable());
    }

    @Test
    void waiterBobCannotIncreaseStock() {
        assertThrows(SecurityException.class,
                () -> service.increaseStock(bob, "item1", 4));
    }

    @Test
    void chefCannotIncreaseStock() {
        assertThrows(SecurityException.class,
                () -> service.increaseStock(chef, "item1", 2));
    }

    @Test
    void increaseStock_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.increaseStock(alice, "BAD_ID", 5));
    }

    @Test
    void capacityLimitIsRespected() {
        service.increaseStock(alice, "item1", 999);
        assertEquals(20, invRepo.findById("item1").orElseThrow().getStockLevel());
    }

    // -------------------------------------------------------
    // GET STOCK LEVEL
    // -------------------------------------------------------

    @Test
    void getStockLevel_returnsCorrect() {
        assertEquals(10, service.getStockLevel("item1"));
    }

    @Test
    void getStockLevel_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getStockLevel("BAD_ID"));
    }
}

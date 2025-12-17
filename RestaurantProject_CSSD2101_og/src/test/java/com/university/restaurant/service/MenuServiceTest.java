package com.university.restaurant.service;

import com.university.restaurant.model.menu.*;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.Chef;
import com.university.restaurant.repository.InMemoryRestaurantAuditRepo;
import com.university.restaurant.repository.InMemoryMenuRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuServiceTest {

    InMemoryMenuRepo repo;
    InMemoryRestaurantAuditRepo audits;
    MenuService service;

    Manager manager = new Manager("m1", "Alice");
    Waiter waiter = new Waiter("w1", "Bob");
    Chef chef = new Chef("c1", "Charles");

    @BeforeEach
    void setup() {
        repo = new InMemoryMenuRepo();
        audits = new InMemoryRestaurantAuditRepo ();
        service = new MenuService(repo, audits);
    }

    // ------------------------------------------
    // ADD MENU ITEM
    // ------------------------------------------

    @Test
    void managerCanAddMenuItem() {
        MenuItem burger = new Entree(
                "i1",
                "Burger",
                "Beef Burger",
                12.0,
                DietaryType.REGULAR,
                List.of("beef", "bun", "cheese"),
                10
        );

        service.addMenuItem(manager, burger);

        assertEquals(burger, repo.findById("i1").orElseThrow());
        assertEquals(1, audits.all().size());
    }

    @Test
    void waiterCannotAddMenuItem() {
        MenuItem drink = new Drink(
                "i2",
                "Coke",
                "Soda",
                3.0,
                false
        );

        assertThrows(SecurityException.class,
                () -> service.addMenuItem(waiter, drink));
    }

    @Test
    void chefCannotAddMenuItem() {
        MenuItem iceCream = new Dessert(
                "i3",
                "Ice Cream",
                "Vanilla scoop",
                6.0,
                DietaryType.REGULAR,
                List.of("milk")
        );

        assertThrows(SecurityException.class,
                () -> service.addMenuItem(chef, iceCream));
    }

    // ------------------------------------------
    // UPDATE PRICE
    // ------------------------------------------

    @Test
    void managerCanUpdatePrice() {
        MenuItem entree = new Entree(
                "i1",
                "Pasta",
                "Creamy pasta",
                10.0,
                DietaryType.REGULAR,
                List.of("pasta", "cream"),
                8
        );

        repo.save(entree);

        service.updatePrice(manager, "i1", 15.0);

        MenuItem updated = repo.findById("i1").orElseThrow();
        assertEquals(15.0, updated.calculatePrice());
        assertEquals(1, audits.all().size());
    }

    @Test
    void updatePrice_itemNotFound_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updatePrice(manager, "bad-id", 10.0));
    }

    @Test
    void waiterCannotUpdatePrice() {
        MenuItem entree = new Entree(
                "i1",
                "Salad",
                "Fresh salad",
                8.0,
                DietaryType.VEGAN,
                List.of("lettuce", "tomato"),
                5
        );

        repo.save(entree);

        assertThrows(SecurityException.class,
                () -> service.updatePrice(waiter, "i1", 20.0));
    }

    // ------------------------------------------
    // LIST AVAILABLE ITEMS
    // ------------------------------------------

    @Test
    void listMenuAvailableItems_returnsOnlyAvailable() {

        MenuItem availableEntree = new Entree(
                "i1",
                "Soup",
                "Tomato soup",
                5.0,
                DietaryType.REGULAR,
                List.of("tomato"),
                2
        );

        MenuItem unavailableDrink = new Drink(
                "i2",
                "Wine",
                "Red wine",
                12.0,
                true
        );
        unavailableDrink.setAvailable(false);

        repo.save(availableEntree);
        repo.save(unavailableDrink);

        List<MenuItem> results = service.listMenuAvailableItems();

        assertEquals(1, results.size());
        assertEquals("i1", results.getFirst().getId());
    }
}

//package com.university.restaurant.model.menu;

//public class ComboTest {
//}
// ==================== ComboTest.java ====================
package com.university.restaurant.model.menu;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ComboTest {

    @Test
    void testConstructorAndGetters() {
        Entree burger = new Entree("E001", "Burger", "Beef", 10.00,
                DietaryType.REGULAR, Arrays.asList("beef"), 15);
        Drink soda = new Drink("DR001", "Soda", "Cola", 2.00, false);

        Combo combo = new Combo("C001", "Meal Deal", "Burger and drink",
                Arrays.asList(burger, soda), 10.0);

        assertEquals("C001", combo.getId());
        assertEquals("Meal Deal", combo.getName());
        assertEquals("Burger and drink", combo.getDescription());
        assertEquals(MenuCategory.COMBO, combo.getCategory());
        assertEquals(2, combo.getItems().size());
        assertTrue(combo.isAvailable());
    }

    @Test
    void testCalculatePriceWithDiscount() {
        Entree entree = new Entree("E001", "Burger", "Beef", 10.00,
                DietaryType.REGULAR, Arrays.asList("beef"), 15);
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);

        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(entree, drink), 10.0);

        // Total = 12.00, with 10% discount = 10.80
        assertEquals(10.80, combo.calculatePrice(), 0.001);
    }

    @Test
    void testCalculatePriceNoDiscount() {
        Drink drink1 = new Drink("DR001", "Water", "Bottled", 1.50, false);
        Drink drink2 = new Drink("DR002", "Juice", "Orange", 3.50, false);

        Combo combo = new Combo("C001", "Drinks", "Two drinks",
                Arrays.asList(drink1, drink2), 0.0);

        assertEquals(5.00, combo.calculatePrice(), 0.001);
    }

    @Test
    void testRequiresKitchenPrepTrue() {
        Entree entree = new Entree("E001", "Burger", "Beef", 10.00,
                DietaryType.REGULAR, Arrays.asList("beef"), 15);
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);

        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(entree, drink), 10.0);

        assertTrue(combo.requiresKitchenPrep());
    }

    @Test
    void testRequiresKitchenPrepFalse() {
        Drink drink1 = new Drink("DR001", "Water", "Bottled", 1.50, false);
        Drink drink2 = new Drink("DR002", "Soda", "Cola", 2.00, false);

        Combo combo = new Combo("C001", "Drinks", "Two drinks",
                Arrays.asList(drink1, drink2), 0.0);

        assertFalse(combo.requiresKitchenPrep());
    }

    @Test
    void testGetRequiredIngredientsFromMultipleItems() {
        Entree entree = new Entree("E001", "Burger", "Beef", 10.00,
                DietaryType.REGULAR, Arrays.asList("beef", "bun"), 15);
        Dessert dessert = new Dessert("D001", "Cake", "Chocolate", 5.00,
                DietaryType.REGULAR, Arrays.asList());

        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(entree, dessert), 15.0);

        java.util.List<String> ingredients = combo.getRequiredIngredients();
        assertTrue(ingredients.contains("beef"));
        assertTrue(ingredients.contains("bun"));
        assertTrue(ingredients.contains("cake"));
    }

    @Test
    void testGetRequiredIngredientsDistinct() {
        Drink drink1 = new Drink("DR001", "Cola", "Soda", 2.00, false);
        Drink drink2 = new Drink("DR002", "Cola", "Soda", 2.00, false);

        Combo combo = new Combo("C001", "Double", "Two colas",
                Arrays.asList(drink1, drink2), 0.0);

        java.util.List<String> ingredients = combo.getRequiredIngredients();
        assertEquals(1, ingredients.size());
        assertEquals("cola", ingredients.get(0));
    }

    @Test
    void testGetItemsDefensiveCopy() {
        ArrayList<MenuItem> items = new ArrayList<>();
        items.add(new Drink("DR001", "Soda", "Cola", 2.00, false));

        Combo combo = new Combo("C001", "Combo", "Desc", items, 10.0);

        items.add(new Drink("DR002", "Water", "Bottled", 1.50, false));
        assertEquals(1, combo.getItems().size());
    }

    @Test
    void testGetItemsImmutable() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);
        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(drink), 10.0);

        assertThrows(UnsupportedOperationException.class,
                () -> combo.getItems().add(new Drink("DR002", "Water", "B", 1.50, false)));
    }

    @Test
    void testCopyWithPriceThrowsException() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);
        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(drink), 10.0);

        assertThrows(UnsupportedOperationException.class,
                () -> combo.copyWithPrice(5.00));
    }

    @Test
    void testSetAvailable() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);
        Combo combo = new Combo("C001", "Combo", "Desc",
                Arrays.asList(drink), 10.0);

        combo.setAvailable(false);
        assertFalse(combo.isAvailable());
    }

    @Test
    void testToString() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.00, false);
        Combo combo = new Combo("C001", "Special", "Desc",
                Arrays.asList(drink), 0.0);

        String expected = "Combo[C001: Special | $0.00 | Available]";
        assertEquals(expected, combo.toString());
    }
}

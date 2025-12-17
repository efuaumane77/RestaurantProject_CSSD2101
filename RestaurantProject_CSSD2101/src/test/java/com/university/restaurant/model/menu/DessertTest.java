//package com.university.restaurant.model.menu;

//public class DessertTest {
//}

// ==================== DessertTest.java ====================
package com.university.restaurant.model.menu;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class DessertTest {

    @Test
    void testConstructorAndGetters() {
        Dessert dessert = new Dessert("D001", "Cake", "Chocolate cake",
                8.99, DietaryType.REGULAR, Arrays.asList("milk", "eggs"));

        assertEquals("D001", dessert.getId());
        assertEquals("Cake", dessert.getName());
        assertEquals("Chocolate cake", dessert.getDescription());
        assertEquals(MenuCategory.DESSERT, dessert.getCategory());
        assertTrue(dessert.isAvailable());
    }

    @Test
    void testCalculatePrice() {
        Dessert dessert = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.VEGAN, Arrays.asList());
        assertEquals(5.99, dessert.calculatePrice(), 0.001);
    }

    @Test
    void testRequiresKitchenPrep() {
        Dessert dessert = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.REGULAR, Arrays.asList());
        assertTrue(dessert.requiresKitchenPrep());
    }

    @Test
    void testGetRequiredIngredients() {
        Dessert dessert = new Dessert("D001", "Chocolate CAKE", "Desc", 5.99,
                DietaryType.REGULAR, Arrays.asList());
        assertEquals(Arrays.asList("chocolate cake"), dessert.getRequiredIngredients());
    }

    @Test
    void testGetAllergensDefensiveCopy() {
        ArrayList<String> allergens = new ArrayList<>(Arrays.asList("milk"));
        Dessert dessert = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.REGULAR, allergens);

        allergens.add("eggs");
        assertEquals(1, dessert.getAllergens().size());
        assertThrows(UnsupportedOperationException.class,
                () -> dessert.getAllergens().add("soy"));
    }

    @Test
    void testCopyWithPrice() {
        Dessert original = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.VEGETARIAN, Arrays.asList("milk"));
        MenuItem copy = original.copyWithPrice(7.99);

        assertEquals(7.99, copy.calculatePrice(), 0.001);
        assertEquals(5.99, original.calculatePrice(), 0.001);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
    }

    @Test
    void testSetAvailable() {
        Dessert dessert = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.REGULAR, Arrays.asList());
        dessert.setAvailable(false);
        assertFalse(dessert.isAvailable());
    }

    @Test
    void testToString() {
        Dessert dessert = new Dessert("D001", "Cake", "Desc", 5.99,
                DietaryType.REGULAR, Arrays.asList());
        String expected = "Dessert[D001: Cake | $5.99 | Available]";
        assertEquals(expected, dessert.toString());
    }

    @Test
    void testAllDietaryTypes() {
        Dessert regular = new Dessert("D001", "Cake1", "Desc", 5.99,
                DietaryType.REGULAR, Arrays.asList());
        Dessert vegan = new Dessert("D002", "Cake2", "Desc", 5.99,
                DietaryType.VEGAN, Arrays.asList());
        Dessert vegetarian = new Dessert("D003", "Cake3", "Desc", 5.99,
                DietaryType.VEGETARIAN, Arrays.asList());
        Dessert glutenFree = new Dessert("D004", "Cake4", "Desc", 5.99,
                DietaryType.GLUTEN_FREE, Arrays.asList());

        // Just verify they construct without errors
        assertNotNull(regular);
        assertNotNull(vegan);
        assertNotNull(vegetarian);
        assertNotNull(glutenFree);
    }
}



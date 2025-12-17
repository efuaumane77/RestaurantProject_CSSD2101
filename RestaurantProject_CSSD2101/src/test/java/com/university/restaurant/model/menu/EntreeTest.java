//package com.university.restaurant.model.menu;

//public class EntreeTest {
//}
// ==================== EntreeTest.java ====================
package com.university.restaurant.model.menu;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class EntreeTest {

    @Test
    void testConstructorAndGetters() {
        Entree entree = new Entree("E001", "Burger", "Beef burger", 12.99,
                DietaryType.REGULAR, Arrays.asList("beef", "bun"), 15);

        assertEquals("E001", entree.getId());
        assertEquals("Burger", entree.getName());
        assertEquals("Beef burger", entree.getDescription());
        assertEquals(MenuCategory.ENTREE, entree.getCategory());
        assertEquals(15, entree.getPrepTimeMinutes());
        assertTrue(entree.isAvailable());
    }

    @Test
    void testCalculatePrice() {
        Entree entree = new Entree("E001", "Salad", "Green", 8.99,
                DietaryType.VEGAN, Arrays.asList("lettuce"), 5);
        assertEquals(8.99, entree.calculatePrice(), 0.001);
    }

    @Test
    void testRequiresKitchenPrep() {
        Entree entree = new Entree("E001", "Pasta", "Spaghetti", 10.99,
                DietaryType.VEGETARIAN, Arrays.asList("pasta"), 20);
        assertTrue(entree.requiresKitchenPrep());
    }

    @Test
    void testGetRequiredIngredientsDefensiveCopy() {
        ArrayList<String> ingredients = new ArrayList<>(Arrays.asList("chicken"));
        Entree entree = new Entree("E001", "Chicken", "Grilled", 14.99,
                DietaryType.REGULAR, ingredients, 25);

        ingredients.add("rice");
        assertEquals(1, entree.getRequiredIngredients().size());
        assertThrows(UnsupportedOperationException.class,
                () -> entree.getRequiredIngredients().add("beans"));
    }

    @Test
    void testGetPrepTimeMinutes() {
        Entree entree = new Entree("E001", "Steak", "Ribeye", 24.99,
                DietaryType.REGULAR, Arrays.asList("beef"), 30);
        assertEquals(30, entree.getPrepTimeMinutes());
    }

    @Test
    void testCopyWithPrice() {
        Entree original = new Entree("E001", "Pizza", "Margherita", 11.99,
                DietaryType.VEGETARIAN, Arrays.asList("dough", "cheese"), 18);
        MenuItem copy = original.copyWithPrice(9.99);

        assertEquals(9.99, copy.calculatePrice(), 0.001);
        assertEquals(11.99, original.calculatePrice(), 0.001);
        assertEquals(18, ((Entree) copy).getPrepTimeMinutes());
        assertEquals(2, ((Entree) copy).getRequiredIngredients().size());
    }

    @Test
    void testSetAvailable() {
        Entree entree = new Entree("E001", "Soup", "Tomato", 6.99,
                DietaryType.VEGAN, Arrays.asList("tomato"), 10);
        entree.setAvailable(false);
        assertFalse(entree.isAvailable());
    }

    @Test
    void testToString() {
        Entree entree = new Entree("E001", "Burger", "Beef", 12.99,
                DietaryType.REGULAR, Arrays.asList("beef"), 15);
        String expected = "Entree[E001: Burger | $12.99 | Available]";
        assertEquals(expected, entree.toString());
    }
}
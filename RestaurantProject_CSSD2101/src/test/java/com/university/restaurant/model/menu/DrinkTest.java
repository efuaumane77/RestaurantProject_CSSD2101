//package com.university.restaurant.model.menu;

//public class DrinkTest {
//}

// ==================== DrinkTest.java ====================
package com.university.restaurant.model.menu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DrinkTest {

    @Test
    void testConstructorAndGetters() {
        Drink drink = new Drink("DR001", "Beer", "Cold beer", 6.99, true);
        assertEquals("DR001", drink.getId());
        assertEquals("Beer", drink.getName());
        assertEquals("Cold beer", drink.getDescription());
        assertEquals(MenuCategory.DRINK, drink.getCategory());
        assertTrue(drink.isAvailable());
    }

    @Test
    void testCalculatePrice() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.99, false);
        assertEquals(2.99, drink.calculatePrice(), 0.001);
    }

    @Test
    void testRequiresKitchenPrep() {
        Drink drink = new Drink("DR001", "Water", "Bottled", 1.99, false);
        assertFalse(drink.requiresKitchenPrep());
    }

    @Test
    void testGetRequiredIngredients() {
        Drink drink = new Drink("DR001", "Orange JUICE", "Fresh", 3.99, false);
        assertEquals(java.util.List.of("orange juice"), drink.getRequiredIngredients());
    }

    @Test
    void testRequiresAgeVerificationAlcoholic() {
        Drink alcoholic = new Drink("DR001", "Wine", "Red wine", 8.99, true);
        assertTrue(alcoholic.requiresAgeVerification());
    }

    @Test
    void testRequiresAgeVerificationNonAlcoholic() {
        Drink nonAlcoholic = new Drink("DR001", "Juice", "Apple", 3.99, false);
        assertFalse(nonAlcoholic.requiresAgeVerification());
    }

    @Test
    void testCopyWithPrice() {
        Drink original = new Drink("DR001", "Beer", "Lager", 5.99, true);
        MenuItem copy = original.copyWithPrice(4.99);

        assertEquals(4.99, copy.calculatePrice(), 0.001);
        assertEquals(5.99, original.calculatePrice(), 0.001);
        assertTrue(((Drink) copy).requiresAgeVerification());
    }

    @Test
    void testSetAvailable() {
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.99, false);
        drink.setAvailable(false);
        assertFalse(drink.isAvailable());
    }

    @Test
    void testToString() {
        Drink drink = new Drink("DR001", "Water", "Bottled", 1.99, false);
        String expected = "Drink[DR001: Water | $1.99 | Available]";
        assertEquals(expected, drink.toString());
    }
}

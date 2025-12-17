//package com.university.restaurant.model.order;

//public class OrderTest {
//}

// ==================== OrderTest.java ====================
package com.university.restaurant.model.order;

import com.university.restaurant.model.menu.*;
        import com.university.restaurant.model.payment.PaymentMethod;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testConstructor() {
        Order order = new Order(5, "W001");

        assertNotNull(order.getId());
        assertEquals(5, order.getTableNumber());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertEquals(0, order.getItems().size());
    }

    @Test
    void testAddItemSuccess() {
        Order order = new Order(5, "W001");
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.99, false);

        order.addItem(drink);
        assertEquals(1, order.getItems().size());
    }

    @Test
    void testAddItemUnavailable() {
        Order order = new Order(5, "W001");
        Drink drink = new Drink("DR001", "Soda", "Cola", 2.99, false);
        drink.setAvailable(false);

        assertThrows(IllegalStateException.class, () -> order.addItem(drink));
    }

    @Test
    void testCalculateTotal() {
        Order order = new Order(5, "W001");
        order.addItem(new Drink("DR001", "Soda", "Cola", 2.99, false));
        order.addItem(new Dessert("D001", "Cake", "Choc", 5.99,
                DietaryType.REGULAR, Arrays.asList()));

        assertEquals(8.98, order.calculateTotal(), 0.001);
    }

    @Test
    void testUpdateStatus() {
        Order order = new Order(5, "W001");
        order.updateStatus(OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void testProcessPaymentSuccess() {
        Order order = new Order(5, "W001");
        order.addItem(new Drink("DR001", "Soda", "Cola", 2.99, false));
        order.updateStatus(OrderStatus.SERVED);

        order.processPayment(PaymentMethod.CASH);

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertNotNull(order.getPayment());
        assertEquals(2.99, order.getPayment().getAmount(), 0.001);
    }

    @Test
    void testProcessPaymentNotServed() {
        Order order = new Order(5, "W001");
        order.addItem(new Drink("DR001", "Soda", "Cola", 2.99, false));

        assertThrows(IllegalArgumentException.class,
                () -> order.processPayment(PaymentMethod.CASH));
    }

    @Test
    void testRequiresKitchenPrepTrue() {
        Order order = new Order(5, "W001");
        order.addItem(new Entree("E001", "Burger", "Beef", 10.00,
                DietaryType.REGULAR, Arrays.asList("beef"), 15));

        assertTrue(order.requiresKitchenPrep());
    }

    @Test
    void testRequiresKitchenPrepFalse() {
        Order order = new Order(5, "W001");
        order.addItem(new Drink("DR001", "Soda", "Cola", 2.99, false));

        assertFalse(order.requiresKitchenPrep());
    }

    @Test
    void testGetItemsImmutable() {
        Order order = new Order(5, "W001");

        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(new Drink("DR001", "S", "C", 2.99, false)));
    }

    @Test
    void testToString() {
        Order order = new Order(5, "W001");
        String result = order.toString();

        assertTrue(result.contains("Table=5"));
        assertTrue(result.contains("PENDING"));
    }
}


//package com.university.restaurant.model.reservation;

//public class CustomerTest {
//}
// ==================== CustomerTest.java ====================
package com.university.restaurant.model.reservation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testConstructorAndGetters() {
        Customer customer = new Customer("John Doe", "555-1234", "john@email.com");

        assertEquals("John Doe", customer.getName());
    }

    @Test
    void testToString() {
        Customer customer = new Customer("Jane Smith", "555-5678", "jane@email.com");
        String result = customer.toString();

        assertTrue(result.contains("Jane Smith"));
        assertTrue(result.contains("555-5678"));
        assertTrue(result.contains("jane@email.com"));
    }
}
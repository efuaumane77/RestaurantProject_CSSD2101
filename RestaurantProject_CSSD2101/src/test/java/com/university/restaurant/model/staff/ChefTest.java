//package com.university.restaurant.model.staff;

//public class ChefTest {
//}

// ==================== STAFF PACKAGE TESTS ====================
// ==================== ChefTest.java ====================
package com.university.restaurant.model.staff;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ChefTest {
    @Test
    void testRecordComponents() {
        Chef chef = new Chef("C001", "Gordon Ramsay");

        assertEquals("C001", chef.id());
        assertEquals("Gordon Ramsay", chef.name());
    }

    @Test
    void testImplementsStaffRole() {
        Chef chef = new Chef("C001", "Jamie Oliver");
        assertTrue(chef instanceof StaffRole);
    }

    @Test
    void testEquality() {
        Chef chef1 = new Chef("C001", "Chef A");
        Chef chef2 = new Chef("C001", "Chef A");
        Chef chef3 = new Chef("C002", "Chef B");

        assertEquals(chef1, chef2);
        assertNotEquals(chef1, chef3);
    }

    @Test
    void testToString() {
        Chef chef = new Chef("C001", "Test Chef");
        assertTrue(chef.toString().contains("C001"));
        assertTrue(chef.toString().contains("Test Chef"));
    }
}

//package com.university.restaurant.model.staff;

//public class WaiterTest {
//}

// ==================== WaiterTest.java ====================
package com.university.restaurant.model.staff;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class WaiterTest {
    @Test
    void testRecordComponents() {
        Waiter waiter = new Waiter("W001", "Sarah Connor");

        assertEquals("W001", waiter.id());
        assertEquals("Sarah Connor", waiter.name());
    }

    @Test
    void testImplementsStaffRole() {
        Waiter waiter = new Waiter("W001", "John Wick");
        assertTrue(waiter instanceof StaffRole);
    }

    @Test
    void testEquality() {
        Waiter waiter1 = new Waiter("W001", "Waiter A");
        Waiter waiter2 = new Waiter("W001", "Waiter A");
        Waiter waiter3 = new Waiter("W002", "Waiter B");

        assertEquals(waiter1, waiter2);
        assertNotEquals(waiter1, waiter3);
    }
}



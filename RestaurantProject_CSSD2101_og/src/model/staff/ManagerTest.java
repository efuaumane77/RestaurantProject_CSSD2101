//package com.university.restaurant.model.staff;

//public class ManagerTest {
//}

// ==================== ManagerTest.java ====================
package com.university.restaurant.model.staff;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ManagerTest {
    @Test
    void testRecordComponents() {
        Manager manager = new Manager("M001", "Alice Johnson");

        assertEquals("M001", manager.id());
        assertEquals("Alice Johnson", manager.name());
    }

    @Test
    void testImplementsStaffRole() {
        Manager manager = new Manager("M001", "Bob Smith");
        assertTrue(manager instanceof StaffRole);
    }

    @Test
    void testEquality() {
        Manager manager1 = new Manager("M001", "Manager A");
        Manager manager2 = new Manager("M001", "Manager A");
        Manager manager3 = new Manager("M002", "Manager B");

        assertEquals(manager1, manager2);
        assertNotEquals(manager1, manager3);
    }
}

//package com.university.restaurant.model.reservation;

//public class ReservationTest {
//}

package com.university.restaurant.model.reservation;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void testConstructor() {
        Customer customer = new Customer("John Doe", "555-1234", "john@email.com");
        LocalDateTime time = LocalDateTime.of(2024, 12, 25, 19, 0);
        Reservation reservation = new Reservation(customer, time, 4);

        assertNotNull(reservation.getId());
        assertEquals(time, reservation.getReservationTime());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertEquals(-1, reservation.getAssignedTable());
    }

    @Test
    void testAssignTable() {
        Customer customer = new Customer("John Doe", "555-1234", "john@email.com");
        Reservation reservation = new Reservation(customer, LocalDateTime.now(), 4);

        reservation.assignTable(5);

        assertEquals(5, reservation.getAssignedTable());
        assertEquals(ReservationStatus.SEATED, reservation.getStatus());
    }

}

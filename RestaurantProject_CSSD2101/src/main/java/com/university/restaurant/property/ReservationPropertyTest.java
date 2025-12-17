package com.university.restaurant.property;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.university.restaurant.model.reservation.Customer;
import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.model.reservation.ReservationStatus;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Property-based tests for Reservation.
 */
@RunWith(JUnitQuickcheck.class)
public class ReservationPropertyTest {

    /**
     * Property: New reservations should always be CONFIRMED.
     */
    @Property
    public void newReservationsShouldBeConfirmed(int partySize) {
        // Arrange
        int safePartySize = Math.abs(partySize % 20) + 1;
        Customer customer = new Customer("John Doe", "555-1234", "john@example.com");
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        // Act
        Reservation reservation = new Reservation(customer, time, safePartySize);

        // Assert
        assertEquals("New reservations should be CONFIRMED", 
                    ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    /**
     * Property: Active reservations should have CONFIRMED or SEATED status.
     */
    @Property
    public void activeReservationsShouldHaveCorrectStatus(int partySize) {
        // Arrange
        int safePartySize = Math.abs(partySize % 20) + 1;
        Customer customer = new Customer("Jane Doe", "555-5678", "jane@example.com");
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        Reservation reservation = new Reservation(customer, time, safePartySize);

        // Assert
        assertTrue("New reservations should be active", reservation.isActive());
        
        // Act - Cancel
        reservation.updateStatus(ReservationStatus.CANCELLED);
        
        // Assert
        assertFalse("Cancelled reservations should not be active", reservation.isActive());
    }

    /**
     * Property: Party size should always be positive.
     */
    @Property
    public void partySizeShouldBePositive(int partySize) {
        // Arrange
        int safePartySize = Math.abs(partySize % 100) + 1;
        Customer customer = new Customer("Test User", "555-0000", "test@example.com");
        LocalDateTime time = LocalDateTime.now().plusHours(2);

        // Act
        Reservation reservation = new Reservation(customer, time, safePartySize);

        // Assert
        assertTrue("Party size should be positive", reservation.getPartySize() > 0);
    }

    /**
     * Property: Reservation ID should be unique.
     */
    @Property
    public void reservationIdsShouldBeUnique(int count) {
        // Arrange
        int safeCount = Math.abs(count % 100) + 1;
        Customer customer = new Customer("Test User", "555-0000", "test@example.com");
        LocalDateTime time = LocalDateTime.now().plusHours(2);
        
        java.util.Set<java.util.UUID> ids = new java.util.HashSet<>();

        // Act
        for (int i = 0; i < safeCount; i++) {
            Reservation reservation = new Reservation(customer, time, 4);
            ids.add(reservation.getId());
        }

        // Assert
        assertEquals("All reservation IDs should be unique", safeCount, ids.size());
    }
}

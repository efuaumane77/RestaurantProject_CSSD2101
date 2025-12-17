package com.university.restaurant.service;

import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.model.reservation.ReservationStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.Chef;
import com.university.restaurant.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    InMemoryReservationRepo repo;
    InMemoryRestaurantAuditRepo audits;
    ReservationService service;

    Manager manager = new Manager("m1", "Bob");
    Waiter waiter = new Waiter("w1", "Alice");
    Chef chef = new Chef("c1", "Charles");

    LocalDateTime time = LocalDateTime.now().plusDays(1);

    @BeforeEach
    void setup() {
        repo = new InMemoryReservationRepo();
        audits = new InMemoryRestaurantAuditRepo();
        service = new ReservationService(repo, audits);
    }

    // -------------------------------------------------------------
    // CREATE RESERVATION
    // -------------------------------------------------------------

    @Test
    void managerCanCreateReservation() {
        Reservation r = service.createReservation(
                manager, "John", "555-1111", "john@email.com",
                4, time
        );

        assertNotNull(r);
        assertEquals("John", r.toString().contains("John") ? "John" : null);
        assertEquals(1, audits.all().size());
        assertEquals(r, repo.findById(r.getId()).orElseThrow());
    }

    @Test
    void waiterCanCreateReservation() {
        Reservation r = service.createReservation(
                waiter, "Sarah", "555-2222", "sarah@email.com",
                2, time
        );

        assertNotNull(r);
        assertEquals(1, audits.all().size());
    }

    @Test
    void chefCannotCreateReservation() {
        assertThrows(SecurityException.class,
                () -> service.createReservation(
                        chef, "Mike", "555-3333", "mike@mail.com",
                        3, time));
    }

    // -------------------------------------------------------------
    // CANCEL RESERVATION
    // -------------------------------------------------------------

    @Test
    void managerCanCancelReservation() {
        Reservation r = service.createReservation(
                manager, "Bob", "555-7777", "bob@mail.com",
                3, time
        );

        boolean result = service.cancelReservation(manager, r.getId().toString());

        assertTrue(result);
        assertEquals(ReservationStatus.CANCELLED,
                repo.findById(r.getId()).orElseThrow().getStatus());

        assertEquals(2, audits.all().size()); // create + cancel
    }

    @Test
    void waiterCanCancelReservation() {
        Reservation r = service.createReservation(
                manager, "Leo", "555-9090", "leo@mail.com",
                5, time
        );

        boolean result = service.cancelReservation(waiter, r.getId().toString());

        assertTrue(result);
        assertEquals(ReservationStatus.CANCELLED,
                repo.findById(r.getId()).orElseThrow().getStatus());
    }

    @Test
    void chefCannotCancelReservation() {
        Reservation r = service.createReservation(
                manager, "Tim", "555-4444", "tim@mail.com",
                2, time
        );

        assertThrows(SecurityException.class,
                () -> service.cancelReservation(chef, r.getId().toString()));
    }

    @Test
    void cancelReservation_returnsFalseIfMissing() {
        boolean result = service.cancelReservation(manager, UUID.randomUUID().toString());
        assertFalse(result);
        assertEquals(0, audits.all().size()); // no audit added
    }

    // -------------------------------------------------------------
    // FIND RESERVATION
    // -------------------------------------------------------------

    @Test
    void findReservation_returnsCorrect() {
        Reservation r = service.createReservation(
                manager, "Oliver", "555-1234", "oliver@mail.com",
                3, time
        );

        Reservation found = service.findReservation(r.getId().toString());

        assertEquals(r, found);
    }

    @Test
    void findReservation_notFound_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findReservation(UUID.randomUUID().toString()));
    }
}


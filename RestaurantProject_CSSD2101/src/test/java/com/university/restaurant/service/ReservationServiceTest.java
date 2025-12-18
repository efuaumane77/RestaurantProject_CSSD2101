package com.university.restaurant.service;

import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.model.reservation.ReservationStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.Chef;
import com.university.restaurant.repository.ReservationRepository;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepo;

    @Mock
    private RestaurantAuditLogRepository auditRepo;

    private ReservationService service;
    private Manager manager;
    private Waiter waiter;
    private Chef chef;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationRepo, auditRepo);
        manager = new Manager("m1", "Alice");
        waiter = new Waiter("w1", "Bob");
        chef = new Chef("c1", "Charlie");
        when(auditRepo.tailHash()).thenReturn("GENESIS");
    }

    @Test
    void createReservation_withWaiterRole_shouldSucceed() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        Reservation reservation = service.createReservation(
            waiter, "John Doe", "555-1234", "john@example.com", 4, time
        );

        assertNotNull(reservation);
        assertEquals(4, reservation.getPartySize());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        verify(reservationRepo).save(reservation);
        verify(auditRepo).append(any());
    }

    @Test
    void createReservation_withManagerRole_shouldSucceed() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        Reservation reservation = service.createReservation(
            manager, "Jane Doe", "555-5678", "jane@example.com", 2, time
        );

        assertNotNull(reservation);
        verify(reservationRepo).save(reservation);
    }

    @Test
    void createReservation_withChefRole_shouldThrowSecurityException() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        assertThrows(SecurityException.class, () -> {
            service.createReservation(chef, "Test", "555-0000", "test@example.com", 4, time);
        });
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void cancelReservation_existingReservation_shouldReturnTrue() {
        Reservation reservation = createTestReservation();
        UUID id = reservation.getId();

        when(reservationRepo.findById(id)).thenReturn(Optional.of(reservation));

        boolean result = service.cancelReservation(waiter, id.toString());

        assertTrue(result);
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        verify(reservationRepo).save(reservation);
        verify(auditRepo).append(any());
    }

    @Test
    void cancelReservation_nonExistentReservation_shouldReturnFalse() {
        UUID randomId = UUID.randomUUID();
        when(reservationRepo.findById(randomId)).thenReturn(Optional.empty());

        boolean result = service.cancelReservation(waiter, randomId.toString());

        assertFalse(result);
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void findReservation_existingReservation_shouldReturnReservation() {
        Reservation reservation = createTestReservation();
        UUID id = reservation.getId();

        when(reservationRepo.findById(id)).thenReturn(Optional.of(reservation));

        Reservation found = service.findReservation(id.toString());

        assertNotNull(found);
        assertEquals(id, found.getId());
    }

    @Test
    void findReservation_nonExistentReservation_shouldThrowException() {
        UUID randomId = UUID.randomUUID();
        when(reservationRepo.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.findReservation(randomId.toString());
        });
    }

    private Reservation createTestReservation() {
        return new Reservation(
            new com.university.restaurant.model.reservation.Customer("Test", "555-0000", "test@example.com"),
            LocalDateTime.now().plusDays(1),
            4
        );
    }
}

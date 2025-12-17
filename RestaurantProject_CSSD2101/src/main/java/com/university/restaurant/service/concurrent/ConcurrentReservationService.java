package com.university.restaurant.service.concurrent;

import com.university.restaurant.chain.reservation.ReservationPermissionChain;
import com.university.restaurant.model.reservation.Customer;
import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.model.reservation.ReservationStatus;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.ReservationServicePort;
import com.university.restaurant.repository.ReservationRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.locks.StampedLock;

/**
 * Thread-safe ReservationService using StampedLock.
 */
public class ConcurrentReservationService implements ReservationServicePort {

    private final ReservationRepository repo;
    private final RestaurantAuditLogRepository audits;
    private final ReservationPermissionChain permissionChain = new ReservationPermissionChain();
    private final StampedLock lock = new StampedLock();

    public ConcurrentReservationService(ReservationRepository repo, RestaurantAuditLogRepository audits) {
        this.repo = repo;
        this.audits = audits;
    }

    @Override
    public Reservation createReservation(StaffRole actor, String name, String phone, String email,
                                         int partySize, LocalDateTime time) {
        permissionChain.check(actor, "create a reservation");

        long stamp = lock.writeLock();
        try {
            Customer customer = new Customer(name, phone, email);
            Reservation reservation = new Reservation(customer, time, partySize);

            repo.save(reservation);

            audits.append(new RestaurantAuditEntry(
                    actor.id(),
                    actor.getClass().getSimpleName(),
                    "CREATE_RESERVATION",
                    customer.getName(),
                    reservation.getId().toString(),
                    "PARTY_SIZE: " + partySize,
                    audits.tailHash()
            ));

            return reservation;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public boolean cancelReservation(StaffRole actor, String reservationId) {
        permissionChain.check(actor, "cancel a reservation");

        long stamp = lock.writeLock();
        try {
            UUID id = UUID.fromString(reservationId);
            Reservation reservation = repo.findById(id).orElse(null);

            if (reservation == null) {
                return false;
            }

            reservation.updateStatus(ReservationStatus.CANCELLED);
            repo.save(reservation);

            audits.append(new RestaurantAuditEntry(
                    actor.id(),
                    actor.getClass().getSimpleName(),
                    "CANCEL_RESERVATION",
                    null,
                    reservation.getId().toString(),
                    "RESERVATION_TIME: " + reservation.getReservationTime(),
                    audits.tailHash()
            ));

            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Reservation findReservation(String reservationId) {
        // Optimistic read
        long stamp = lock.tryOptimisticRead();
        UUID id = UUID.fromString(reservationId);
        Reservation reservation = repo.findById(id).orElse(null);
        
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                reservation = repo.findById(id).orElse(null);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        
        return reservation;
    }
}

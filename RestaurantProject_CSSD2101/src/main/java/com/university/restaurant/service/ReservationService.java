package com.university.restaurant.service;

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

/**
 * <p>
 * Concrete implementation of {@link ReservationServicePort}, responsible for
 * managing customer reservations within the restaurant system. This service
 * enforces role-based permissions, applies domain rules, persists reservation
 * state, and records audit entries for all reservation-related actions.
 * </p>
 *
 * <p>
 * Following Hexagonal Architecture principles, this class depends only on
 * repository interfaces and the permission chain rather than specific storage
 * or UI details. All business logic related to reservations is centralized here.
 * </p>
 */
public class ReservationService implements ReservationServicePort {

    private final ReservationRepository repo;
    private final RestaurantAuditLogRepository audits;
    private final ReservationPermissionChain permissionChain = new ReservationPermissionChain();

    /**
     * Constructs a new ReservationService with the required repositories.
     *
     * @param repo   the repository used to save and retrieve reservations
     * @param audits the repository responsible for appending audit log entries
     */
    public ReservationService(ReservationRepository repo, RestaurantAuditLogRepository audits) {
        this.repo = repo;
        this.audits = audits;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation performs the following steps:</p>
     * <ul>
     *     <li>Validates that the actor has permission to create reservations</li>
     *     <li>Builds a new {@link Customer} instance</li>
     *     <li>Constructs a {@link Reservation} with the provided details</li>
     *     <li>Saves the reservation to persistent storage</li>
     *     <li>Writes an audit log entry describing the action</li>
     * </ul>
     */
    @Override
    public Reservation createReservation(StaffRole actor, String name, String phone, String email,
                                         int partySize, LocalDateTime time) {

        permissionChain.check(actor, "create a reservation");

        Customer customer = new Customer(name, phone, email);

        Reservation reservation = new Reservation(customer, time, partySize);

        // Save to repository
        repo.save(reservation);

        // Audit creation
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
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method:
     * </p>
     * <ul>
     *     <li>Checks permission for the actor</li>
     *     <li>Attempts to retrieve the reservation</li>
     *     <li>Returns {@code false} if it does not exist</li>
     *     <li>Updates its status to {@link ReservationStatus#CANCELLED}</li>
     *     <li>Saves the modified reservation</li>
     *     <li>Records an audit log entry</li>
     * </ul>
     */
    @Override
    public boolean cancelReservation(StaffRole actor, String reservationId) {

        permissionChain.check(actor, "cancel a reservation");

        UUID id = UUID.fromString(reservationId);

        Reservation reservation = repo.findById(id).orElse(null);

        if (reservation == null) {
            return false; // Nothing to cancel
        }

        // Update reservation status
        reservation.updateStatus(ReservationStatus.CANCELLED);

        // Save updated reservation
        repo.save(reservation);

        // Audit cancellation
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
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves a reservation by its identifier, throwing an exception if no such
     * reservation exists.
     * </p>
     */
    @Override
    public Reservation findReservation(String reservationId) {

        UUID id = UUID.fromString(reservationId);

        return repo.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found: " + reservationId)
                );
    }
}

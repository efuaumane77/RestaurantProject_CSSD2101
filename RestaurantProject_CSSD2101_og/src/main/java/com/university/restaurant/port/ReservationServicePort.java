package com.university.restaurant.port;

import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.model.staff.StaffRole;

import java.time.LocalDateTime;

/**
 * <p>
 * Defines the contract for reservation-management use cases within the
 * restaurant system. This port abstracts the creation, cancellation,
 * and lookup of reservations in accordance with Hexagonal Architecture
 * principles, ensuring the domain logic is independent of storage or UI details.
 * </p>
 *
 * <p>
 * Implementations must enforce permission checks (e.g., only managers
 * and waiters may manage reservations), maintain domain rules, persist
 * reservations, and record audit information when applicable.
 * </p>
 */
public interface ReservationServicePort {

    /**
     * <p>
     * Creates a new reservation for a customer. Implementations must:
     * </p>
     * <ul>
     *     <li>Verify that the acting staff member has permission</li>
     *     <li>Construct a {@link Reservation} using customer details and party size</li>
     *     <li>Persist the reservation through the repository</li>
     *     <li>Record an audit entry describing the action</li>
     * </ul>
     *
     * @param actor     the staff role initiating the reservation
     * @param name      the customer's name
     * @param phone     the customer's phone number
     * @param email     the customer's email address
     * @param partySize the number of guests
     * @param time      the date and time of the reservation
     * @return the created {@link Reservation} object
     *
     * @throws SecurityException         if the actor lacks permission
     * @throws IllegalArgumentException  if input details or timing are invalid
     */
    Reservation createReservation(StaffRole actor,
                                  String name,
                                  String phone,
                                  String email,
                                  int partySize,
                                  LocalDateTime time);

    /**
     * <p>
     * Cancels an existing reservation, if it exists and is eligible.
     * Implementations must:
     * </p>
     * <ul>
     *     <li>Ensure permission checks pass</li>
     *     <li>Locate the reservation by ID</li>
     *     <li>Update its status (e.g., to CANCELLED)</li>
     *     <li>Persist the updated reservation</li>
     *     <li>Record an audit trail event</li>
     * </ul>
     *
     * @param actor         the staff role performing the cancellation
     * @param reservationId the UUID string identifying the reservation
     * @return {@code true} if the reservation was found and cancelled,
     *         {@code false} if no reservation existed with the given ID
     *
     * @throws SecurityException if the actor is not authorized
     */
    boolean cancelReservation(StaffRole actor, String reservationId);

    /**
     * <p>
     * Retrieves an existing {@link Reservation} by its identifier.
     * If the reservation does not exist, an exception is thrown.
     * </p>
     *
     * @param reservationId the reservation's UUID in string form
     * @return the matching {@link Reservation}
     *
     * @throws IllegalArgumentException if the reservation does not exist
     */
    Reservation findReservation(String reservationId);
}

package com.university.restaurant.chain.reservation;

import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * A concrete handler in the Reservation Chain-of-Responsibility that authorizes
 * reservation-related actions for {@link Waiter} roles.
 * </p>
 *
 * <p>
 * Waiters are permitted to create and cancel reservations, so when this handler
 * matches a {@code Waiter} role, the {@link #handle(StaffRole, String)} method
 * performs no action—indicating that the request is authorized.
 * </p>
 *
 * <p>
 * If the role is not a waiter, this handler does not participate and the request
 * moves on to the next handler in the chain.
 * </p>
 */
public class WaiterReservationHandler implements ReservationPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing the given role.
     * <p>
     * This handler applies only to instances of {@link Waiter}.
     * </p>
     *
     * @param role the staff member attempting the reservation operation
     * @return true if the role is a waiter
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Waiter;
    }

    /**
     * Grants permission for the reservation action.
     * <p>
     * Since waiters are authorized to manage reservations, this method intentionally
     * performs no logic—authorization is implicitly granted when this handler matches.
     * </p>
     *
     * @param role   the actor performing the action
     * @param action descriptive label of the attempted reservation operation
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Waiter allowed to manage reservations → do nothing
    }
}

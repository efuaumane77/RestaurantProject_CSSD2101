package com.university.restaurant.chain.reservation;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback handler in the Reservation Chain-of-Responsibility. This handler is
 * invoked only when no prior handler in the chain claims responsibility for
 * the given {@link StaffRole}.
 * </p>
 *
 * <p>
 * Its purpose is to enforce a strict default-deny security policy: any role
 * that is not explicitly authorized to manage reservations (such as waiters,
 * hosts, or chefs—depending on system policy) will be blocked here.
 * </p>
 *
 * <p>
 * When this handler executes, it always throws a {@link SecurityException}
 * indicating the actor lacks permission to perform the requested action.
 * </p>
 */
public class DenyReservationHandler implements ReservationPermissionHandler {

    /**
     * Always returns {@code true}, making this handler the universal fallback.
     * <p>
     * Because all earlier handlers in the chain handle specific authorized
     * roles, this handler activates only when no authorization rule matched.
     * </p>
     *
     * @param role the staff member attempting a reservation action
     * @return always true, signaling this handler can process any role
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true; // fallback handler for all remaining roles
    }

    /**
     * Always throws a {@link SecurityException}, denying the reservation
     * operation for any unauthorized role.
     *
     * @param role   the actor attempting the action
     * @param action the description of the attempted action
     * @throws SecurityException always thrown to block unauthorized access
     */
    @Override
    public void handle(StaffRole role, String action) {
        throw new SecurityException(
                role.getClass().getSimpleName() + " is not allowed to " + action
        );
    }
}

package com.university.restaurant.chain.reservation;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * A concrete handler in the Reservation Chain-of-Responsibility that authorizes
 * reservation-related operations for {@link Manager} roles.
 * </p>
 *
 * <p>
 * This handler grants permission by matching roles that are instances of
 * {@code Manager}. When matched, the handler performs no action and simply
 * allows the request to proceed.
 * </p>
 *
 * <p>
 * All reservation operations not handled by this class will fall through to the
 * next handler in the chain — typically the default-deny handler.
 * </p>
 */
public class ManagerReservationHandler implements ReservationPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing the given role.
     * <p>
     * Returns {@code true} if and only if the role is a {@link Manager}, indicating
     * that this handler should authorize the action.
     * </p>
     *
     * @param role the staff member attempting the reservation operation
     * @return true when the actor is a Manager
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Grants permission for the reservation action.
     * <p>
     * Because managers are fully authorized to manage reservations, this method
     * intentionally performs no checks or side-effects — permission is granted by
     * reaching this handler in the chain.
     * </p>
     *
     * @param role   the actor performing the action
     * @param action a textual description of the attempted operation
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager allowed → do nothing (authorization successful)
    }
}

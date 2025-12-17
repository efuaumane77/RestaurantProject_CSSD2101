package com.university.restaurant.chain.reservation;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Implements the Chain of Responsibility pattern for reservation-related
 * authorization. This chain determines whether a staff member has permission
 * to perform a given reservation action (e.g., creating or canceling a reservation).
 * </p>
 *
 * <p>
 * The chain processes handlers in the following order:
 * </p>
 * <ol>
 *     <li>{@link ManagerReservationHandler} — Managers are fully authorized.</li>
 *     <li>{@link WaiterReservationHandler} — Waiters may manage reservations.</li>
 *     <li>{@link DenyReservationHandler} — Fallback that denies all other roles.</li>
 * </ol>
 *
 * <p>
 * Once a handler reports that it can handle the given role, its {@code handle()}
 * method is invoked. Authorization success or failure is determined entirely by
 * the handler.
 * </p>
 */
public class ReservationPermissionChain {

    private final List<ReservationPermissionHandler> handlers;

    /**
     * Constructs the reservation permission chain using a fixed, ordered set of handlers.
     * <p>
     * The order is important: earlier handlers have higher authority and exclusive access
     * to process matching roles before the request reaches the fallback deny handler.
     * </p>
     */
    public ReservationPermissionChain() {
        this.handlers = List.of(
                new ManagerReservationHandler(),
                new WaiterReservationHandler(),
                new DenyReservationHandler()  // fallback
        );
    }

    /**
     * Evaluates whether a given staff role is permitted to perform a reservation action.
     * <p>
     * The method iterates through the handler list and finds the first handler whose
     * {@code canHandle()} method returns {@code true}. That handler is then responsible
     * for approving or rejecting the request via its {@code handle()} method.
     * </p>
     *
     * @param role   the staff member attempting the action
     * @param action a descriptive label of the attempted operation
     */
    public void check(StaffRole role, String action) {
        for (ReservationPermissionHandler h : handlers) {
            if (h.canHandle(role)) {
                h.handle(role, action);
                return;
            }
        }
    }
}

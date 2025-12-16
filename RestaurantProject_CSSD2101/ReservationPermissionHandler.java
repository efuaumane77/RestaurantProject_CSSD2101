package com.university.restaurant.chain.reservation;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Defines the handler interface for the Reservation Chain of Responsibility.
 * Implementations determine whether they are responsible for authorizing a given
 * reservation-related action based on the actor's role.
 * </p>
 *
 * <p>
 * Each handler has two responsibilities:
 * </p>
 * <ul>
 *     <li>
 *         Indicate whether it can process the given role using {@link #canHandle(StaffRole)}.
 *     </li>
 *     <li>
 *         Grant or deny the action inside {@link #handle(StaffRole, String)}.
 *         A denial is typically implemented by throwing a {@link SecurityException}.
 *     </li>
 * </ul>
 *
 * <p>
 * Handlers are arranged into an ordered chain. Only the first handler whose
 * {@code canHandle()} method returns {@code true} will be invoked, ensuring clear,
 * role-based permission resolution.
 * </p>
 */
public interface ReservationPermissionHandler {

    /**
     * Determines whether this handler is responsible for evaluating permissions
     * for the given staff role.
     *
     * @param role the staff member attempting to perform an action
     * @return true if this handler should process the role, false otherwise
     */
    boolean canHandle(StaffRole role);

    /**
     * Performs the authorization logic for a reservation action.
     * <p>
     * Implementations may either:
     * </p>
     * <ul>
     *     <li>Allow the action (typically by doing nothing), or</li>
     *     <li>Deny the action (typically by throwing a {@link SecurityException}).</li>
     * </ul>
     *
     * @param role   the staff member performing the action
     * @param action a human-readable description of the attempted operation
     */
    void handle(StaffRole role, String action);
}

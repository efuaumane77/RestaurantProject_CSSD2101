package com.university.restaurant.chain.payment;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Defines a handler in the Payment Chain-of-Responsibility. Each implementation
 * determines whether it is responsible for processing permission checks for a
 * specific type of staff role (e.g., Manager, Waiter). If the handler
 * acknowledges the role via {@link #canHandle(StaffRole)}, its
 * {@link #handle(StaffRole, String)} method will execute the permission logic.
 * </p>
 *
 * <p>
 * Handlers are evaluated sequentially by {@code PaymentPermissionChain}. The
 * first handler whose {@link #canHandle(StaffRole)} method returns {@code true}
 * is used. Handlers may:
 * <ul>
 *     <li>Allow the action (do nothing)</li>
 *     <li>Deny the action (throw {@link SecurityException})</li>
 * </ul>
 * </p>
 *
 * <p>
 * This design allows flexible permission control, where different staff roles
 * have different authorization levels for payment operations.
 * </p>
 */
public interface PaymentPermissionHandler {

    /**
     * Determines whether this handler can evaluate permissions for the given staff role.
     *
     * @param role the role attempting to perform a payment operation
     * @return true if this handler should handle the role, false otherwise
     */
    boolean canHandle(StaffRole role);

    /**
     * Executes the permission logic for the given staff role. Implementations
     * may either allow the action (performing no operation) or deny it by
     * throwing a {@link SecurityException}.
     *
     * @param role   the staff role attempting the action
     * @param action a descriptive name of the action being performed
     */
    void handle(StaffRole role, String action);
}

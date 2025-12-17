package com.university.restaurant.chain.payment;

import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler in the Payment Chain-of-Responsibility that grants payment
 * permissions specifically to {@link Waiter} roles.
 * </p>
 *
 * <p>
 * This handler's {@link #canHandle(StaffRole)} method returns {@code true}
 * only when the actor is a {@code Waiter}. When selected by the chain,
 * {@link #handle(StaffRole, String)} performs no operation, meaning the waiter
 * is authorized to complete payment-related actions.
 * </p>
 *
 * <p>
 * If the actor is not a waiter, control passes to the next handler in the chain,
 * eventually reaching the fallback {@code DenyPaymentHandler} if no authorized
 * role is matched.
 * </p>
 */
public class WaiterPaymentHandler implements PaymentPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing permissions
     * for the given role. This handler applies only to {@link Waiter} roles.
     *
     * @param role the role attempting to perform a payment operation
     * @return true if the role is a Waiter, false otherwise
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Waiter;
    }

    /**
     * Grants permission to the waiter for the requested action.
     * <p>
     * Since waiters are authorized to handle payment actions, this method
     * intentionally performs no validation or exception throwing.
     * </p>
     *
     * @param role   the waiter performing the action
     * @param action the description of the attempted action
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Waiter allowed → no action required
    }
}

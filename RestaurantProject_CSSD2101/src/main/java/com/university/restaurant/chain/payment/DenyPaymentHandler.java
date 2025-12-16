package com.university.restaurant.chain.payment;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback handler in the Payment Permission Chain. This handler will process
 * any role that was not approved by earlier handlers in the chain.
 * </p>
 *
 * <p>
 * Since only certain roles (e.g., {@code Manager} and {@code Waiter})
 * are allowed to complete or view payments, this handler unconditionally
 * denies access for all other roles.
 * </p>
 *
 * <p>
 * The chain is structured so that this handler appears last. If execution
 * reaches this point, no previous handler has authorized the role, so a
 * {@link SecurityException} is thrown.
 * </p>
 */
public class DenyPaymentHandler implements PaymentPermissionHandler {

    /**
     * Indicates that this handler can handle all roles.
     * This ensures that if no earlier handler grants permission,
     * this fallback handler will always execute.
     *
     * @param role the staff role attempting the payment-related action
     * @return always {@code true}, as this is the fallback handler
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true; // fallback for all remaining roles
    }

    /**
     * Always denies the attempted payment action by throwing a
     * {@link SecurityException}. Used as the final step in the chain
     * when no valid permission match is found.
     *
     * @param role   the role attempting the unauthorized action
     * @param action description of the attempted payment operation
     * @throws SecurityException always thrown to deny access
     */
    @Override
    public void handle(StaffRole role, String action) {
        throw new SecurityException(
                role.getClass().getSimpleName() + " is NOT allowed to " + action
        );
    }
}

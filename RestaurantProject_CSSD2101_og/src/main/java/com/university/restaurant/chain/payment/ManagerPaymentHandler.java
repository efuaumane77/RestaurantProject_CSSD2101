package com.university.restaurant.chain.payment;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler that authorizes payment-related actions for users
 * with the {@link Manager} role. As part of the Payment Permission Chain,
 * this handler is evaluated before the fallback denial handler.
 * </p>
 *
 * <p>
 * If the actor is a {@code Manager}, the chain stops here and permission
 * is granted. The {@link #handle(StaffRole, String)} method performs no
 * action because managers automatically have full access.
 * </p>
 */
public class ManagerPaymentHandler implements PaymentPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing
     * the given role. This returns {@code true} only when the role
     * is an instance of {@link Manager}.
     *
     * @param role the staff role attempting the payment action
     * @return {@code true} if the role is a {@code Manager}; otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Grants permission for the specified payment-related action.
     * Since managers are fully authorized, this method intentionally
     * performs no operations and simply allows the chain to succeed.
     *
     * @param role   the actor performing the action
     * @param action a human-readable description of the attempted operation
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager always allowed
    }
}

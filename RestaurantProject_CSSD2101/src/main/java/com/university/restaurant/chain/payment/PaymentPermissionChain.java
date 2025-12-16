package com.university.restaurant.chain.payment;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * A Chain-of-Responsibility implementation for enforcing payment-related
 * permissions in the restaurant system. The chain is composed of multiple
 * {@link PaymentPermissionHandler} instances, each responsible for deciding
 * whether a specific staff role is authorized to perform a payment operation.
 * </p>
 *
 * <p>
 * The order of handlers defines the precedence of permissions:
 * <ul>
 *     <li>{@code ManagerPaymentHandler} – full access</li>
 *     <li>{@code WaiterPaymentHandler} – limited access</li>
 *     <li>{@code DenyPaymentHandler} – fallback denial for all other roles</li>
 * </ul>
 * </p>
 *
 * <p>
 * The chain stops at the first handler whose {@link PaymentPermissionHandler#canHandle(StaffRole)}
 * method returns {@code true}. That handler then executes the permission logic
 * through {@link PaymentPermissionHandler#handle(StaffRole, String)}.
 * </p>
 */
public class PaymentPermissionChain {

    /** Ordered list of handlers representing permission levels (manager → waiter → deny). */
    private final List<PaymentPermissionHandler> handlers;

    /**
     * Constructs the chain with its default handler sequence:
     * manager, waiter, and finally the fallback denial handler.
     */
    public PaymentPermissionChain() {
        this.handlers = List.of(
                new ManagerPaymentHandler(),
                new WaiterPaymentHandler(),
                new DenyPaymentHandler() // fallback
        );
    }

    /**
     * Executes the permission check for the given role and action. The first handler
     * capable of processing the role is invoked. If no handler explicitly allows
     * the action, the fallback handler will throw a {@link SecurityException}.
     *
     * @param role   the staff member attempting the action
     * @param action a descriptive string of what action is being attempted
     */
    public void check(StaffRole role, String action) {
        for (PaymentPermissionHandler h : handlers) {
            if (h.canHandle(role)) {
                h.handle(role, action);
                return;
            }
        }
    }
}

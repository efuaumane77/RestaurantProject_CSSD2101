package com.university.restaurant.chain.order;

import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler in the Order Permission Chain that authorizes order-related
 * operations for users with the {@link Waiter} role.
 * </p>
 *
 * <p>
 * Waiters are allowed to perform standard order actions such as:
 * </p>
 * <ul>
 *     <li>placing orders</li>
 *     <li>updating order status (e.g., marking as served)</li>
 *     <li>viewing order details</li>
 * </ul>
 *
 * <p>
 * This handler grants permissions when the provided {@link StaffRole}
 * is an instance of {@link Waiter}. When matched, the {@link #handle} method
 * simply allows the action to proceed without throwing exceptions.
 * </p>
 *
 * <p>
 * If the role is not a Waiter, this handler declines responsibility and the
 * permission chain continues to the next handler
 * (e.g., {@link DenyOrderHandler}).
 * </p>
 */
public class WaiterOrderHandler implements OrderPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing the given
     * role. It returns {@code true} only if the role is a {@link Waiter}.
     *
     * @param role the staff role attempting the order-related action
     * @return {@code true} if this handler should evaluate the role, otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Waiter;
    }

    /**
     * Approves the requested action for waiters. Since waiters are permitted to
     * perform order operations, this method intentionally contains no logic
     * and allows execution to proceed.
     *
     * @param role   the Waiter performing the action
     * @param action a textual description of the attempted action
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Waiter allowed → do nothing
    }
}

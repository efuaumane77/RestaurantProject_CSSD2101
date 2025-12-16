package com.university.restaurant.chain.order;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Implements the Chain of Responsibility pattern for authorizing order-related
 * actions within the restaurant system. This chain determines whether a given
 * {@link StaffRole} has the required permission to perform operations such as:
 * </p>
 * <ul>
 *     <li>placing orders</li>
 *     <li>updating order status</li>
 *     <li>retrieving order details</li>
 * </ul>
 *
 * <p>
 * Handlers in the chain are evaluated in the following order:
 * </p>
 * <ol>
 *     <li>{@link ManagerOrderHandler} — grants full order permissions to managers</li>
 *     <li>{@link WaiterOrderHandler} — grants order permissions to waiters</li>
 *     <li>{@link DenyOrderHandler} — fallback that denies all unauthorized roles</li>
 * </ol>
 *
 * <p>
 * The first handler whose {@link OrderPermissionHandler#canHandle(StaffRole)}
 * method returns {@code true} becomes responsible for authorizing (or denying)
 * the action.
 * </p>
 */
public class OrderPermissionChain {

    private final List<OrderPermissionHandler> handlers;

    /**
     * Constructs the order permission chain using a predefined set of handlers.
     * <p>
     * The order of handlers is significant and determines how permission
     * resolution flows through the chain.
     * </p>
     */
    public OrderPermissionChain() {
        this.handlers = List.of(
                new ManagerOrderHandler(),
                new WaiterOrderHandler(),
                new DenyOrderHandler()  // fallback
        );
    }

    /**
     * <p>
     * Performs a permission check for the given staff role and intended action.
     * </p>
     *
     * <p>
     * The method iterates through the permission handlers in order. The first
     * handler that reports it can handle the role becomes responsible for
     * making the authorization decision.
     * </p>
     *
     * <p>
     * If the selected handler denies access (e.g., by throwing a
     * {@link SecurityException}), the caller is expected to handle it.
     * </p>
     *
     * @param role   the staff role attempting the order operation
     * @param action a human-readable description of the attempted action
     *
     * @throws SecurityException if the action is not permitted for the role
     */
    public void check(StaffRole role, String action) {
        for (OrderPermissionHandler h : handlers) {
            if (h.canHandle(role)) {
                h.handle(role, action);
                return;
            }
        }
    }
}

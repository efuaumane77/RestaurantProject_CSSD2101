package com.university.restaurant.chain.order;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Defines the contract for handlers participating in the Order Permission Chain.
 * These handlers implement the Chain of Responsibility pattern to determine
 * whether a {@link StaffRole} is allowed to perform order-related operations
 * such as placing orders, updating order status, or retrieving order information.
 * </p>
 *
 * <p>
 * Each handler decides:
 * </p>
 * <ul>
 *     <li>whether it is responsible for evaluating a given role (via
 *         {@link #canHandle(StaffRole)})</li>
 *     <li>whether the requested action should be permitted or denied (via
 *         {@link #handle(StaffRole, String)})</li>
 * </ul>
 *
 * <p>
 * Handlers are invoked sequentially by {@link OrderPermissionChain}. The first
 * handler whose {@code canHandle} method returns {@code true} becomes
 * responsible for authorizing or rejecting the action.
 * </p>
 */
public interface OrderPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing the given
     * staff role. Only one handler in the chain should authorize a specific role.
     *
     * @param role the staff role attempting an order-related operation
     * @return {@code true} if this handler should process the role
     */
    boolean canHandle(StaffRole role);

    /**
     * Performs authorization logic for the given role and action. Implementations
     * may either:
     * <ul>
     *     <li>allow the action (typically by doing nothing), or</li>
     *     <li>deny the action by throwing a {@link SecurityException}</li>
     * </ul>
     *
     * @param role   the role attempting the order action
     * @param action a human-readable description of the action being attempted
     *
     * @throws SecurityException if the action is not permitted for the role
     */
    void handle(StaffRole role, String action);
}

package com.university.restaurant.chain.menu;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Defines the contract for handlers used in the Menu Permission Chain, which
 * implements the Chain of Responsibility pattern to determine whether a
 * {@link StaffRole} is authorized to perform menu-management actions.
 * </p>
 *
 * <p>
 * Each handler decides:
 * </p>
 * <ul>
 *     <li>whether it is responsible for processing a specific role, via
 *         {@link #canHandle(StaffRole)}</li>
 *     <li>whether the requested action should be allowed or denied, via
 *         {@link #handle(StaffRole, String)}</li>
 * </ul>
 *
 * <p>
 * Handlers are evaluated in sequence by {@link MenuPermissionChain}. The first
 * handler that reports it can handle the given role becomes responsible for
 * authorizing or rejecting the menu action.
 * </p>
 */
public interface MenuPermissionHandler {

    /**
     * Determines whether this handler should evaluate the given staff role.
     * If two handlers return {@code true}, only the first in the chain will run.
     *
     * @param role the staff role attempting the menu operation
     * @return {@code true} if this handler wishes to process the role
     */
    boolean canHandle(StaffRole role);

    /**
     * Performs the permission check for the provided role and operation.
     * <p>
     * Implementations may:
     * </p>
     * <ul>
     *     <li>Silently allow the action (e.g., for managers)</li>
     *     <li>Deny the action by throwing a {@link SecurityException}</li>
     * </ul>
     *
     * @param role   the staff role performing the action
     * @param action a human-readable description of the attempted menu action
     *
     * @throws SecurityException if the action is not permitted
     */
    void handle(StaffRole role, String action);
}

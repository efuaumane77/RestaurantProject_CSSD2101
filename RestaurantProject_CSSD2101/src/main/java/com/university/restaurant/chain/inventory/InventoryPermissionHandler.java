package com.university.restaurant.chain.inventory;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Represents a single handler in the Inventory Permission Chain, implementing
 * the Chain of Responsibility pattern. Each handler determines whether it is
 * responsible for evaluating permission for a given {@link StaffRole}, and if
 * so, performs the appropriate authorization logic.
 * </p>
 *
 * <p>
 * Handlers are evaluated in sequence by {@link InventoryPermissionChain}. The
 * first handler whose {@link #canHandle(StaffRole)} method returns {@code true}
 * becomes responsible for deciding whether the requested action is permitted.
 * </p>
 *
 * <p>
 * Typical handlers:
 * </p>
 * <ul>
 *     <li><strong>ManagerInventoryHandler</strong> — grants access for managers</li>
 *     <li><strong>DenyInventoryHandler</strong> — fallback that rejects all others</li>
 * </ul>
 */
public interface InventoryPermissionHandler {

    /**
     * Determines whether this handler is responsible for evaluating permission
     * for the specified staff role.
     *
     * @param role the staff role attempting to perform an inventory action
     * @return {@code true} if this handler should process the role, otherwise {@code false}
     */
    boolean canHandle(StaffRole role);

    /**
     * Performs the permission check for the given role and action. Implementations
     * may either silently allow the action (by doing nothing) or deny the request,
     * typically by throwing a {@link SecurityException}.
     *
     * @param role   the staff role attempting the action
     * @param action a human-readable description of the attempted inventory operation
     *
     * @throws SecurityException if this role is not allowed to perform the action
     */
    void handle(StaffRole role, String action);
}

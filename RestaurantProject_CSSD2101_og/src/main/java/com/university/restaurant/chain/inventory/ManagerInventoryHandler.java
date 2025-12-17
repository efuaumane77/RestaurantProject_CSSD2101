package com.university.restaurant.chain.inventory;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler in the Inventory Permission Chain responsible for
 * authorizing inventory-related actions for users with the {@link Manager}
 * role. Managers have full access to inventory operations such as increasing
 * stock, reducing stock, or modifying availability.
 * </p>
 *
 * <p>
 * This handler succeeds when the provided {@link StaffRole} is an instance of
 * {@link Manager}. If so, the {@link #handle(StaffRole, String)} method performs
 * no action, indicating that the operation is allowed.
 * </p>
 *
 * <p>
 * If the role is not a manager, the chain proceeds to the next handler
 * (typically {@link DenyInventoryHandler}), which will reject the action.
 * </p>
 */
public class ManagerInventoryHandler implements InventoryPermissionHandler {

    /**
     * Determines whether this handler applies to the given role.
     * Returns {@code true} only when the role is an instance of {@link Manager}.
     *
     * @param role the staff role being evaluated
     * @return {@code true} if the role is a Manager, otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Allows the requested action without restriction. Since managers have
     * full inventory permissions, no checks or exceptions are required.
     *
     * @param role   the Manager performing the action
     * @param action the textual description of the attempted action
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager allowed → do nothing
    }
}

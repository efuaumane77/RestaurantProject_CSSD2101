package com.university.restaurant.chain.menu;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler responsible for authorizing menu-management actions
 * performed by staff members with the {@link Manager} role.
 * </p>
 *
 * <p>
 * This handler is part of the Menu Permission Chain and implements the
 * Chain of Responsibility pattern. It grants access exclusively to managers,
 * who are the only staff members allowed to modify menu items (e.g., adding
 * new items or updating prices).
 * </p>
 *
 * <p>
 * If a role matches {@link Manager}, this handler approves the action by doing
 * nothing in {@link #handle(StaffRole, String)}. Otherwise, the chain continues
 * to the next handler (typically {@link DenyMenuHandler}).
 * </p>
 */
public class ManagerMenuHandler implements MenuPermissionHandler {

    /**
     * Determines whether this handler is responsible for processing the given
     * role. This method returns {@code true} only if the role is a
     * {@link Manager}.
     *
     * @param role the staff role attempting the menu operation
     * @return {@code true} if the role is a Manager, otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Approves the requested action for managers. Since managers are fully
     * authorized for menu-management operations, this method intentionally
     * contains no logic and performs no checks.
     *
     * @param role   the Manager performing the action
     * @param action a human-readable description of the attempted action
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager allowed → do nothing
    }
}

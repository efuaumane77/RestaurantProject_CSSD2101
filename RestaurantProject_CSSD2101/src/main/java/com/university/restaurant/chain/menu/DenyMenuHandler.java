package com.university.restaurant.chain.menu;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback permission handler in the Menu Permission Chain. This handler is
 * invoked when no previous handler has accepted responsibility for the given
 * {@link StaffRole}. Its purpose is to deny permission for all menu-related
 * operations such as adding menu items or updating prices.
 * </p>
 *
 * <p>
 * The method {@link #canHandle(StaffRole)} always returns {@code true},
 * guaranteeing that this handler will execute if reached. By throwing a
 * {@link SecurityException} inside {@link #handle(StaffRole, String)}, it
 * enforces a strict deny-by-default policy for menu management.
 * </p>
 *
 * <p>
 * This ensures that only explicitly authorized roles (e.g., Manager)
 * can manage the menu, while all other roles—including Waiters, Chefs,
 * and external actors—are automatically rejected.
 * </p>
 */
public class DenyMenuHandler implements MenuPermissionHandler {

    /**
     * Always returns {@code true}, allowing this handler to act as the final
     * fallback in the permission chain.
     *
     * @param role the staff role attempting the menu operation
     * @return {@code true} for any staff role
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true;  // fallback for all remaining roles
    }

    /**
     * Always denies the requested menu action by throwing a
     * {@link SecurityException}. This enforces that only roles explicitly
     * allowed earlier in the chain may perform menu operations.
     *
     * @param role   the staff role attempting the action
     * @param action textual description of the attempted menu operation
     *
     * @throws SecurityException always thrown to deny access
     */
    @Override
    public void handle(StaffRole role, String action) {
        throw new SecurityException(
                role.getClass().getSimpleName() + " is NOT allowed to " + action
        );
    }
}

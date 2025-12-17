package com.university.restaurant.chain.inventory;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback handler in the inventory permission chain. This handler is designed
 * to catch any {@link StaffRole} that is not explicitly handled by a previous
 * permission handler in the chain.
 * </p>
 *
 * <p>
 * By returning {@code true} in {@link #canHandle(StaffRole)}, this handler
 * always matches and therefore acts as the final link in the chain of
 * responsibility. Its sole purpose is to deny access by throwing a
 * {@link SecurityException}.
 * </p>
 *
 * <p>
 * This ensures that any role lacking explicit permission to perform an
 * inventory-related action (e.g., reduce stock, increase stock) will be
 * rejected in a predictable and secure manner.
 * </p>
 */
public class DenyInventoryHandler implements InventoryPermissionHandler {

    /**
     * Always returns {@code true}, making this handler the universal fallback
     * for any staff role not already processed in the chain.
     *
     * @param role the staff role attempting to perform an action
     * @return {@code true} for all roles, indicating this handler can handle them
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true; // fallback for all roles
    }

    /**
     * Always denies the action by throwing a {@link SecurityException}.
     * This enforces the rule that only explicitly allowed roles may perform
     * inventory operations.
     *
     * @param role   the staff role attempting the inventory operation
     * @param action the textual description of the attempted action
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

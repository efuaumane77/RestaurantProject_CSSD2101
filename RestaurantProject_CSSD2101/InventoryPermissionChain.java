package com.university.restaurant.chain.inventory;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Implements the Chain of Responsibility pattern for inventory-related
 * permission checks. Each handler determines whether a particular
 * {@link StaffRole} has the authority to perform a specified inventory action
 * (e.g., reducing stock, increasing stock).
 * </p>
 *
 * <p>
 * The chain is evaluated sequentially. The first handler whose
 * {@link InventoryPermissionHandler#canHandle(StaffRole)} method returns
 * {@code true} becomes responsible for enforcing (or rejecting) the action.
 * </p>
 *
 * <p>
 * The configured chain consists of:
 * </p>
 * <ul>
 *     <li>{@code ManagerInventoryHandler} — grants permission to managers</li>
 *     <li>{@code DenyInventoryHandler} — fallback that denies all other roles</li>
 * </ul>
 *
 * <p>
 * This pattern cleanly separates decision-making and allows new permission
 * rules or staff roles to be added without modifying existing code.
 * </p>
 */
public class InventoryPermissionChain {

    private final List<InventoryPermissionHandler> handlers;

    /**
     * Initializes the chain with a predefined sequence of permission handlers.
     * <p>
     * Order matters: the first handler that reports it can handle the given role
     * will be the one to execute its corresponding logic.
     * </p>
     */
    public InventoryPermissionChain() {
        this.handlers = List.of(
                new ManagerInventoryHandler(),
                new DenyInventoryHandler() // fallback
        );
    }

    /**
     * <p>
     * Executes the permission check for the specified staff role and action.
     * </p>
     *
     * <p>
     * The method iterates through the handler chain and delegates processing to
     * the first handler whose {@code canHandle} method returns {@code true}.
     * </p>
     *
     * <p>
     * If the handler denies permission (e.g., the fallback handler), a
     * {@link SecurityException} will be thrown.
     * </p>
     *
     * @param role   the staff member attempting the inventory operation
     * @param action the human-readable description of the attempted action
     *
     * @throws SecurityException if the action is not permitted for the role
     */
    public void check(StaffRole role, String action) {
        for (InventoryPermissionHandler handler : handlers) {
            if (handler.canHandle(role)) {
                handler.handle(role, action);
                return;
            }
        }
    }
}

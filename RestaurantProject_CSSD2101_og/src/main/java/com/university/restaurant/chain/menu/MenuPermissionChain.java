package com.university.restaurant.chain.menu;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Implements the Chain of Responsibility for menu-management permission checks.
 * This chain determines whether a given {@link StaffRole} is authorized to
 * perform protected menu operations such as adding items or updating prices.
 * </p>
 *
 * <p>
 * The chain evaluates handlers in order:
 * </p>
 * <ul>
 *     <li>{@link ManagerMenuHandler} — grants permission to managers</li>
 *     <li>{@link DenyMenuHandler} — fallback that denies all other roles</li>
 * </ul>
 *
 * <p>
 * The first handler whose {@link MenuPermissionHandler#canHandle(StaffRole)}
 * method returns {@code true} becomes responsible for deciding whether the
 * action is permitted.
 * </p>
 *
 * <p>
 * This design cleanly separates authorization logic from business logic and
 * allows additional roles or rules to be added without modifying existing code.
 * </p>
 */
public class MenuPermissionChain {

    private final List<MenuPermissionHandler> handlers;

    /**
     * Constructs the permission chain with a fixed handler sequence.
     * <p>
     * Order matters: the first handler that supports the role is given control.
     * </p>
     */
    public MenuPermissionChain() {
        this.handlers = List.of(
                new ManagerMenuHandler(),
                new DenyMenuHandler() // fallback
        );
    }

    /**
     * <p>
     * Executes the permission check for the provided staff role and requested
     * menu-management action.
     * </p>
     *
     * <p>
     * The method iterates through the handler list and delegates processing to
     * the first handler that reports it can handle the role. If that handler
     * rejects the request (e.g., via a {@link SecurityException}), the action
     * is denied.
     * </p>
     *
     * @param role   the staff role attempting the operation
     * @param action a human-readable description of the attempted menu action
     *
     * @throws SecurityException if the role lacks permission to perform the action
     */
    public void check(StaffRole role, String action) {
        for (MenuPermissionHandler h : handlers) {
            if (h.canHandle(role)) {
                h.handle(role, action);
                return;
            }
        }
    }
}

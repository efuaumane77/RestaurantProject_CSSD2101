package com.university.restaurant.chain.order;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback handler in the Order Permission Chain. This handler is responsible
 * for denying any order-related action attempted by a {@link StaffRole} that
 * is not explicitly authorized by earlier handlers in the chain.
 * </p>
 *
 * <p>
 * As part of the Chain of Responsibility pattern, this handler's
 * {@link #canHandle(StaffRole)} method always returns {@code true}, ensuring
 * it will be invoked if no previous handler claims responsibility. This
 * provides a strict and predictable deny-by-default security policy.
 * </p>
 *
 * <p>
 * When invoked, {@link #handle(StaffRole, String)} throws a
 * {@link SecurityException}, preventing the requested order operation from
 * being executed.
 * </p>
 */
public class DenyOrderHandler implements OrderPermissionHandler {

    /**
     * Always returns {@code true}, indicating that this fallback handler can
     * handle any role not handled earlier in the chain.
     *
     * @param role the staff member attempting the order-related action
     * @return {@code true} for every possible role
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true; // fallback
    }

    /**
     * Always denies the order action by throwing a {@link SecurityException}.
     * This enforces that only authorized roles (e.g., Manager or Waiter)
     * may perform order operations.
     *
     * @param role   the role attempting the action
     * @param action the description of the attempted order operation
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

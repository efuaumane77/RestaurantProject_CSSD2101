package com.university.restaurant.chain.analytics;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Fallback permission handler within the Analytics Permission Chain.
 * </p>
 *
 * <p>
 * This handler always returns {@code true} for {@link #canHandle(StaffRole)},
 * meaning it acts as the final catch-all step in the chain. If execution reaches
 * this handler, it indicates that no previous handler granted permission.
 * </p>
 *
 * <p>
 * The handler enforces a denial by unconditionally throwing a
 * {@link SecurityException}, preventing unauthorized roles from performing
 * analytics-related actions.
 * </p>
 */
public class DenyAnalyticsHandler implements AnalyticsPermissionHandler {

    /**
     * Always returns {@code true}, making this handler the universal fallback
     * for any role that was not explicitly handled by earlier chain elements.
     *
     * @param role the staff role attempting an analytics operation
     * @return always {@code true}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return true; // fallback
    }

    /**
     * Unconditionally denies the action by throwing a {@link SecurityException}.
     * <p>
     * This method is invoked only when no previous handler authorized the role.
     * </p>
     *
     * @param role   the staff role attempting the operation
     * @param action human-readable description of the analytics action
     *
     * @throws SecurityException always thrown to indicate access is forbidden
     */
    @Override
    public void handle(StaffRole role, String action) {
        throw new SecurityException(
                role.getClass().getSimpleName() + " is NOT allowed to " + action
        );
    }
}


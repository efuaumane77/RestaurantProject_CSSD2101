package com.university.restaurant.chain.analytics;

import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Permission chain responsible for validating whether a given staff role
 * is allowed to perform analytics-related operations such as viewing revenue
 * or top-selling item statistics.
 * </p>
 *
 * <p>
 * This class implements the Chain of Responsibility pattern. Each concrete
 * {@link AnalyticsPermissionHandler} is evaluated in sequence until one
 * determines whether the given {@link StaffRole} is authorized to perform
 * the specified action.
 * </p>
 *
 * <p>
 * The chain typically includes:
 * <ul>
 *     <li>{@code ManagerAnalyticsHandler} — grants access to managers.</li>
 *     <li>{@code DenyAnalyticsHandler} — fallback handler denying all unauthorized roles.</li>
 * </ul>
 * </p>
 */
public class AnalyticsPermissionChain {

    /**
     * Immutable list of handlers forming the analytics permission chain.
     * The order matters: the first handler capable of handling a role
     * determines the outcome.
     */
    private final List<AnalyticsPermissionHandler> handlers;

    /**
     * Constructs the analytics permission chain with the predefined sequence
     * of handlers—starting with manager authorization and ending with a
     * fallback denial handler.
     */
    public AnalyticsPermissionChain() {
        this.handlers = List.of(
                new ManagerAnalyticsHandler(),
                new DenyAnalyticsHandler() // fallback
        );
    }

    /**
     * Executes the permission-checking process for the given role and action.
     * <p>
     * The chain is traversed in order. For each handler:
     * </p>
     * <ul>
     *     <li>If {@code canHandle()} returns true, {@code handle()} is invoked.</li>
     *     <li>The method returns immediately after the first handler processes the role.</li>
     * </ul>
     *
     * @param role   the staff role attempting the action
     * @param action human-readable description of the attempted action
     *
     * @throws SecurityException if no handler grants permission (typically handled
     *                           by {@code DenyAnalyticsHandler})
     */
    public void check(StaffRole role, String action) {
        for (AnalyticsPermissionHandler h : handlers) {
            if (h.canHandle(role)) {
                h.handle(role, action);
                return;
            }
        }
    }
}

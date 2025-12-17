package com.university.restaurant.chain.analytics;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Represents a handler within the Analytics Permission Chain, used to determine
 * whether a given {@link StaffRole} is authorized to perform an analytics-related
 * action such as viewing revenue or top-selling reports.
 * </p>
 *
 * <p>
 * This interface is a key part of the Chain of Responsibility pattern:
 * each implementation checks whether it can evaluate the given role. If so,
 * it performs the authorization logic; otherwise, control passes to the next
 * handler in the chain.
 * </p>
 */
public interface AnalyticsPermissionHandler {

    /**
     * Determines whether this handler is responsible for evaluating
     * the specified staff role.
     *
     * @param role the staff role attempting an analytics action
     * @return {@code true} if this handler can handle the role,
     *         otherwise {@code false}
     */
    boolean canHandle(StaffRole role);

    /**
     * Performs the actual permission handling for the given role and action.
     * <p>
     * Implementations typically:
     * </p>
     * <ul>
     *     <li>authorize the action, doing nothing if valid</li>
     *     <li>or throw a {@link SecurityException} if not permitted</li>
     * </ul>
     *
     * @param role   the staff role performing the action
     * @param action a human-readable description of the analytics action
     *
     * @throws SecurityException if the role is not permitted to perform the action
     */
    void handle(StaffRole role, String action);
}

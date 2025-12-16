package com.university.restaurant.chain.analytics;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler responsible for granting analytics access to
 * {@link Manager} roles. As part of the Chain of Responsibility for analytics
 * permissions, this handler checks whether the staff role is a manager and
 * authorizes the requested analytics action if so.
 * </p>
 *
 * <p>
 * If the role is not a manager, the chain continues to the next handler.
 * If it is a manager, permission is granted and no exception is thrown.
 * </p>
 */
public class ManagerAnalyticsHandler implements AnalyticsPermissionHandler {

    /**
     * Determines whether this handler is responsible for evaluating the given role.
     * This handler only handles instances of {@link Manager}.
     *
     * @param role the staff role attempting an analytics operation
     * @return {@code true} if the role is a {@code Manager}, otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Approves the analytics action for managers.
     * <p>
     * Since managers are fully authorized, this method intentionally performs no action
     * and throws no exception.
     * </p>
     *
     * @param role   the authorized manager
     * @param action the analytics action being attempted
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager allowed → do nothing
    }
}

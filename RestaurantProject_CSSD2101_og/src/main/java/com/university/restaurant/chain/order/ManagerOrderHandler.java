package com.university.restaurant.chain.order;

import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Permission handler in the Order Permission Chain responsible for authorizing
 * order-related actions performed by users with the {@link Manager} role.
 * Managers are granted full privileges to place orders, update order statuses,
 * and retrieve order details.
 * </p>
 *
 * <p>
 * This handler participates in the Chain of Responsibility pattern by checking
 * whether the provided {@link StaffRole} is a {@link Manager}. If so, it allows
 * the operation without restriction by performing no action inside
 * {@link #handle(StaffRole, String)}.
 * </p>
 *
 * <p>
 * If the role is not a Manager, the chain continues to the next handler
 * (typically {@link DenyOrderHandler}), which will make the final authorization
 * decision.
 * </p>
 */
public class ManagerOrderHandler implements OrderPermissionHandler {

    /**
     * Determines whether this handler applies to the given staff role.
     * Returns {@code true} only when the role represents a {@link Manager}.
     *
     * @param role the staff role attempting the order-related operation
     * @return {@code true} if the role is a Manager, otherwise {@code false}
     */
    @Override
    public boolean canHandle(StaffRole role) {
        return role instanceof Manager;
    }

    /**
     * Approves the requested action for managers. Since managers have unrestricted
     * permissions for order operations, this method contains no logic and allows
     * the request to proceed.
     *
     * @param role   the Manager performing the action
     * @param action a textual description of the attempted order operation
     */
    @Override
    public void handle(StaffRole role, String action) {
        // Manager allowed → do nothing
    }
}

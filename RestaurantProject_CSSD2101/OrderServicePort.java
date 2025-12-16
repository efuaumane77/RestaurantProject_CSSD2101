package com.university.restaurant.port;

import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Defines the contract for order-related use cases within the restaurant
 * management system. This port is part of the application layer under a
 * Hexagonal (Ports & Adapters) Architecture, allowing the domain logic to
 * remain independent of specific infrastructure or UI technologies.
 * </p>
 *
 * <p>
 * Implementations of this interface must enforce role-based permissions,
 * perform order lifecycle operations (create, update, retrieve), and ensure
 * domain rules are respected (e.g., item availability, valid status updates).
 * </p>
 */
public interface OrderServicePort {

    /**
     * <p>
     * Places a new order for a given table by a staff member. Implementations
     * should validate permissions, build a new {@link Order}, add the requested
     * {@link MenuItem} objects, and persist the order.
     * </p>
     *
     * @param actor     the staff member initiating the order
     * @param tableId   the identifier of the table (string form, typically numeric)
     * @param items     the list of menu items included in the order
     * @return the created {@link Order} instance
     *
     * @throws SecurityException        if the staff role is not authorized
     * @throws IllegalArgumentException if the tableId is invalid or an item is unavailable
     */
    Order placeOrder(StaffRole actor, String tableId, List<MenuItem> items);

    /**
     * <p>
     * Updates the status of an existing order (e.g., PENDING → SERVED).
     * Implementations must verify permissions, validate the order exists,
     * and ensure the status transition is permitted by domain rules.
     * </p>
     *
     * @param actor     the staff member attempting to modify the order
     * @param orderId   the unique identifier of the order (UUID as String)
     * @param status    the new status name (expected to match {@code OrderStatus})
     *
     * @throws SecurityException         if the actor lacks permission
     * @throws IllegalArgumentException  if the order does not exist or status is invalid
     */
    void updateOrderStatus(StaffRole actor, String orderId, String status);

    /**
     * <p>
     * Retrieves an existing {@link Order} by its identifier. If the order does
     * not exist, an exception should be thrown.
     * </p>
     *
     * @param orderId the unique identifier of the order (UUID as String)
     * @return the corresponding {@link Order}
     *
     * @throws IllegalArgumentException if no such order exists
     */
    Order getOrder(String orderId);
}

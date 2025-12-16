package com.university.restaurant.service;

import com.university.restaurant.chain.order.OrderPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.OrderServicePort;
import com.university.restaurant.repository.OrderRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Service layer implementation of the {@link OrderServicePort}. This class
 * performs all business logic related to creating, modifying, and retrieving
 * orders while enforcing domain rules and permission-based access control.
 * </p>
 *
 * <p>
 * This service is part of the application layer under Hexagonal Architecture.
 * It delegates persistence to {@link OrderRepository}, records audit logs
 * via {@link RestaurantAuditLogRepository}, and enforces role-based security
 * through {@link OrderPermissionChain}.
 * </p>
 */
public class OrderService implements OrderServicePort {

    private final OrderRepository repo;
    private final RestaurantAuditLogRepository audits;
    private final OrderPermissionChain permissions = new OrderPermissionChain();

    /**
     * Constructs a new {@code OrderService} with the required repositories.
     *
     * @param repo   the repository used to persist and retrieve orders
     * @param audits the repository used to append audit log entries
     */
    public OrderService(OrderRepository repo, RestaurantAuditLogRepository audits) {
        this.repo = repo;
        this.audits = audits;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * <ul>
     *     <li>Validates the actor's permissions</li>
     *     <li>Parses the table identifier</li>
     *     <li>Creates a new {@link Order}</li>
     *     <li>Adds all menu items to the order</li>
     *     <li>Saves the order to the repository</li>
     *     <li>Writes an audit log entry</li>
     * </ul>
     * </p>
     */
    @Override
    public Order placeOrder(StaffRole actor, String tableId, List<MenuItem> items) {

        permissions.check(actor, "place an order");

        int tableNum = Integer.parseInt(tableId);

        // Create order
        Order order = new Order(tableNum, actor.id());

        for (MenuItem item : items) {
            order.addItem(item);
        }

        // Save to repository
        repo.save(order);

        // Audit entry
        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "PLACE_ORDER",
                "Order",
                order.getId().toString(),
                "Placed order with %d items".formatted(items.size()),
                audits.tailHash()
        ));

        return order;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This method:
     * <ul>
     *     <li>Validates permissions</li>
     *     <li>Loads the order from persistent storage</li>
     *     <li>Converts the new status string into an {@link OrderStatus}</li>
     *     <li>Updates the order's status</li>
     *     <li>Persists the updated order</li>
     *     <li>Appends an audit log entry</li>
     * </ul>
     * </p>
     */
    @Override
    public void updateOrderStatus(StaffRole actor, String orderId, String newStatus) {

        permissions.check(actor, "update order status");

        UUID id = UUID.fromString(orderId);

        Order order = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());

        order.updateStatus(status);

        repo.save(order);

        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "UPDATE_ORDER_STATUS",
                "Order",
                orderId,
                "Status changed to " + status,
                audits.tailHash()
        ));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Loads an order by ID from the repository. If the order does not exist,
     * an {@link IllegalArgumentException} is thrown.
     * </p>
     */
    @Override
    public Order getOrder(String orderId) {
        UUID id = UUID.fromString(orderId);

        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
}

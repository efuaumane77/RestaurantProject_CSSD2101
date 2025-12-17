package com.university.restaurant.service.concurrent;

import com.university.restaurant.chain.order.OrderPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.OrderServicePort;
import com.university.restaurant.repository.OrderRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous OrderService using CompletableFuture.
 */
public class AsyncOrderService implements OrderServicePort {

    private static final Logger log = LoggerFactory.getLogger(AsyncOrderService.class);
    
    private final OrderRepository repo;
    private final RestaurantAuditLogRepository audits;
    private final OrderPermissionChain permissions = new OrderPermissionChain();
    private final ExecutorService executor;
    private final KitchenService kitchenService;

    public AsyncOrderService(OrderRepository repo, RestaurantAuditLogRepository audits, 
                            KitchenService kitchenService) {
        this.repo = repo;
        this.audits = audits;
        this.kitchenService = kitchenService;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Place order asynchronously with kitchen notification.
     */
    public CompletableFuture<Order> placeOrderAsync(StaffRole actor, String tableId, List<MenuItem> items) {
        return CompletableFuture.supplyAsync(() -> {
            permissions.check(actor, "place an order");

            int tableNum = Integer.parseInt(tableId);
            Order order = new Order(tableNum, actor.id());

            for (MenuItem item : items) {
                order.addItem(item);
            }

            repo.save(order);
            
            log.info("Order {} created asynchronously", order.getId());
            return order;
        }, executor)
        .thenCompose(this::notifyKitchenAsync)
        .thenApply(order -> {
            // Notify UI (in real app, this would use WebSocket)
            notifyUI(order);
            
            // Audit
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
        })
        .exceptionally(ex -> {
            log.error("Failed to place order asynchronously", ex);
            throw new RuntimeException("Order placement failed", ex);
        });
    }

    private CompletableFuture<Order> notifyKitchenAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            if (order.requiresKitchenPrep()) {
                kitchenService.submitOrder(order);
                order.updateStatus(OrderStatus.CONFIRMED);
            }
            return order;
        }, executor);
    }

    private void notifyUI(Order order) {
        // Simulate UI notification
        log.info("UI notified: Order {} status={}", order.getId(), order.getStatus());
    }

    // Synchronous fallback methods
    @Override
    public Order placeOrder(StaffRole actor, String tableId, List<MenuItem> items) {
        try {
            return placeOrderAsync(actor, tableId, items).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to place order", e);
        }
    }

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

    @Override
    public Order getOrder(String orderId) {
        UUID id = UUID.fromString(orderId);
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    public void shutdown() {
        executor.shutdown();
    }
}

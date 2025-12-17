package com.university.restaurant.service.concurrent;

import com.university.restaurant.chain.analytics.AnalyticsPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.repository.OrderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Asynchronous analytics computation service.
 */
public class AsyncAnalyticsService {

    private final OrderRepository orders;
    private final AnalyticsPermissionChain permissions = new AnalyticsPermissionChain();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public AsyncAnalyticsService(OrderRepository orders) {
        this.orders = orders;
    }

    /**
     * Compute top-selling items asynchronously in the background.
     */
    public CompletableFuture<Map<String, Long>> computeTopSellingAsync(StaffRole actor) {
        return CompletableFuture.supplyAsync(() -> {
            permissions.check(actor, "view top-selling analytics");

            List<Order> completed = new ArrayList<>(orders.findByStatus(OrderStatus.PAID));
            completed.addAll(orders.findByStatus(OrderStatus.SERVED));

            return completed.stream()
                    .flatMap(o -> o.getItems().stream())
                    .collect(Collectors.groupingBy(
                            MenuItem::getName,
                            Collectors.counting()
                    ));
        }, executor);
    }

    /**
     * Compute revenue asynchronously.
     */
    public CompletableFuture<Double> computeRevenueTodayAsync(StaffRole actor) {
        return CompletableFuture.supplyAsync(() -> {
            permissions.check(actor, "view revenue analytics");

            LocalDate today = LocalDate.now();

            return orders.findByStatus(OrderStatus.PAID).stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(today))
                    .mapToDouble(Order::calculateTotal)
                    .sum();
        }, executor);
    }

    public void shutdown() {
        executor.shutdown();
    }
}

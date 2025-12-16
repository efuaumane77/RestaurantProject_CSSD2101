package com.university.restaurant.service;

import com.university.restaurant.chain.analytics.AnalyticsPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.AnalyticsServicePort;
import com.university.restaurant.repository.OrderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Service implementation for analytics-related operations such as computing
 * top-selling menu items and daily revenue totals. This class sits in the
 * application/service layer and interacts with the {@link OrderRepository}
 * to retrieve order data.
 * </p>
 *
 * <p>
 * All analytics methods enforce role-based access control via
 * {@link AnalyticsPermissionChain} before performing computations.
 * Unauthorized roles must be blocked by throwing {@link SecurityException}.
 * </p>
 */
public class AnalyticsService implements AnalyticsServicePort {

    /** Repository for retrieving order records used in analytics. */
    private final OrderRepository orders;

    /** Permission chain enforcing which staff roles may access analytics. */
    private final AnalyticsPermissionChain permissions = new AnalyticsPermissionChain();

    /**
     * Constructs the analytics service with the required order repository.
     *
     * @param orders repository used to query order data for analytics
     */
    public AnalyticsService(OrderRepository orders) {
        this.orders = orders;
    }

    /**
     * <p>
     * Computes the list of top-selling menu items by counting how many times
     * each item appears in completed orders. Completed orders include those
     * with status {@link OrderStatus#PAID} or {@link OrderStatus#SERVED}.
     * </p>
     *
     * <p>
     * The result is returned as a map where each key is a menu item name and
     * the value is the number of times that item was ordered.
     * </p>
     *
     * @param actor the staff role requesting analytics access
     * @return a map of item names to sales counts
     *
     * @throws SecurityException if the role is not authorized to view analytics
     */
    @Override
    public Map<String, Long> topSellingItems(StaffRole actor) {

        permissions.check(actor, "view top-selling analytics");

        // Consider only paid or served orders
        List<Order> completed = new ArrayList<>(orders.findByStatus(OrderStatus.PAID));
        completed.addAll(orders.findByStatus(OrderStatus.SERVED));

        return completed.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        MenuItem::getName,     // group by item name
                        Collectors.counting()  // count how many times sold
                ));
    }

    /**
     * <p>
     * Calculates total revenue generated from all paid orders created today.
     * Only orders with status {@link OrderStatus#PAID} are considered, and
     * only if their creation date matches the system's current date.
     * </p>
     *
     * @param actor the staff role requesting analytics access
     * @return revenue total for today's paid orders, or {@code 0.0} if none exist
     *
     * @throws SecurityException if the role is not authorized to view revenue analytics
     */
    @Override
    public double totalRevenueToday(StaffRole actor) {

        permissions.check(actor, "view revenue analytics");

        LocalDate today = LocalDate.now();

        // Filter only PAID orders for today's date
        return orders.findByStatus(OrderStatus.PAID).stream()
                .filter(o -> o.getCreatedAt().toLocalDate().equals(today))
                .mapToDouble(Order::calculateTotal)
                .sum();
    }

}

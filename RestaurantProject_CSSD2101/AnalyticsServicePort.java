package com.university.restaurant.port;

import com.university.restaurant.model.staff.StaffRole;

import java.util.Map;

/**
 * <p>
 * Port interface for analytics-related use cases in the restaurant system.
 * Defines the operations available for retrieving analytical insights such as
 * top-selling menu items and daily revenue. Implementations are typically part
 * of the application/service layer, decoupled from persistence and UI concerns
 * through Hexagonal Architecture (Ports & Adapters).
 * </p>
 *
 * <p>
 * Both methods require a {@link StaffRole} to enforce role-based authorization.
 * Unauthorized roles should be rejected by the permission chain invoked inside
 * the service implementation.
 * </p>
 */
public interface AnalyticsServicePort {

    /**
     * Returns a frequency map of top-selling menu items based on completed orders.
     * <p>
     * Implementations generally consider:
     * </p>
     * <ul>
     *     <li>Orders with status {@code PAID} or {@code SERVED}</li>
     *     <li>Counts aggregated by menu item name</li>
     * </ul>
     *
     * @param actor the staff role requesting analytics access; used to validate permissions
     * @return a mapping of menu item names to the number of times they were sold
     *
     * @throws SecurityException if the role is not authorized to view analytics
     */
    Map<String, Long> topSellingItems(StaffRole actor);

    /**
     * Calculates the total revenue generated from paid orders for the current day.
     * <p>
     * Implementations typically:
     * </p>
     * <ul>
     *     <li>Filter orders by status {@code PAID}</li>
     *     <li>Include only those whose creation timestamp matches the current date</li>
     * </ul>
     *
     * @param actor the staff role requesting the revenue data; used to enforce access control
     * @return the total revenue for the current day as a double value
     *
     * @throws SecurityException if the role is not authorized to view revenue analytics
     */
    double totalRevenueToday(StaffRole actor);
}

package com.university.restaurant.port;

import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.staff.StaffRole;

import java.util.List;

/**
 * <p>
 * Port interface exposing the core menu-management use cases for the restaurant
 * application. This interface defines how the application layer interacts with
 * menu-related functionality, independent of persistence or UI concerns, in
 * alignment with Hexagonal Architecture (Ports & Adapters).
 * </p>
 *
 * <p>
 * Implementations are expected to enforce role-based authorization through
 * a permission chain, ensuring only authorized staff (typically managers)
 * may modify menu items.
 * </p>
 */
public interface MenuServicePort {

    /**
     * Adds a new {@link MenuItem} to the menu.
     *
     * <p>
     * Implementations should:
     * </p>
     * <ul>
     *     <li>Validate actor permissions</li>
     *     <li>Persist the new menu item</li>
     *     <li>Record an audit log entry for traceability</li>
     * </ul>
     *
     * @param actor the staff member attempting to add the item
     * @param item  the menu item to add
     *
     * @throws SecurityException if the actor is not authorized to modify the menu
     */
    void addMenuItem(StaffRole actor, MenuItem item);

    /**
     * Updates the price of an existing menu item.
     *
     * <p>
     * Because many implementations treat {@code MenuItem} as an immutable
     * domain object, updating a price may require replacing the old instance
     * with a new one. The implementing service determines how to handle this.
     * </p>
     *
     * @param actor   the staff member attempting the price update
     * @param itemId  the ID of the item to modify
     * @param newPrice the new price value
     *
     * @throws SecurityException        if the actor lacks permission
     * @throws IllegalArgumentException if the item does not exist
     */
    void updatePrice(StaffRole actor, String itemId, double newPrice);

    /**
     * Returns a list of all menu items that are currently available for ordering.
     * <p>
     * Implementations typically filter out:
     * </p>
     * <ul>
     *     <li>Items marked unavailable due to stock constraints</li>
     *     <li>Items temporarily disabled by staff</li>
     * </ul>
     *
     * @return a list of available menu items
     */
    List<MenuItem> listMenuAvailableItems();

}

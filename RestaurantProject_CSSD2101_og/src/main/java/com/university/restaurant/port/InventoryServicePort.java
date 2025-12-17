package com.university.restaurant.port;

import com.university.restaurant.model.staff.StaffRole;

/**
 * <p>
 * Port interface defining the inventory management use cases available to the
 * application layer. These operations are responsible for updating stock levels
 * of inventory items and querying current stock. Implementations typically
 * enforce business rules such as preventing negative stock and updating related
 * menu item availability.
 * </p>
 *
 * <p>
 * All operations require a {@link StaffRole} to ensure proper authorization,
 * since only certain roles (e.g., Manager) are permitted to modify inventory.
 * Permission validation is expected to be handled by the implementing service
 * via an Inventory Permission Chain.
 * </p>
 */
public interface InventoryServicePort {

    /**
     * Reduces the stock level of an inventory item by a specified quantity.
     * <p>
     * Implementations should:
     * </p>
     * <ul>
     *     <li>Verify the actor is authorized to perform inventory changes</li>
     *     <li>Throw an exception if the item does not exist</li>
     *     <li>Enforce domain rules such as preventing consumption beyond available stock</li>
     *     <li>Persist the updated inventory record</li>
     *     <li>Optionally mark related menu items unavailable when stock hits zero</li>
     * </ul>
     *
     * @param actor  the staff role attempting to perform the stock reduction
     * @param itemId the ID of the inventory item
     * @param qty    the quantity to reduce
     *
     * @throws SecurityException        if the actor is not permitted to modify inventory
     * @throws IllegalArgumentException if the item does not exist
     * @throws IllegalStateException    if the reduction results in negative stock
     */
    void reduceStock(StaffRole actor, String itemId, int qty);

    /**
     * Increases the stock level of an inventory item by a specified quantity.
     * <p>
     * Implementations should:
     * </p>
     * <ul>
     *     <li>Validate actor permissions</li>
     *     <li>Ensure the item exists</li>
     *     <li>Apply capacity limits (e.g., max storage capacity)</li>
     *     <li>Persist the updated stock level</li>
     *     <li>Optionally mark menu items available again if stock becomes positive</li>
     * </ul>
     *
     * @param actor  the staff role attempting the increase
     * @param itemId the ID of the inventory item
     * @param qty    the quantity to add
     *
     * @throws SecurityException        if the actor lacks permission
     * @throws IllegalArgumentException if the item does not exist
     */
    void increaseStock(StaffRole actor, String itemId, int qty);

    /**
     * Retrieves the current stock level for a particular inventory item.
     *
     * @param itemId the ID of the item to query
     * @return the current stock level
     *
     * @throws IllegalArgumentException if the item does not exist
     */
    int getStockLevel(String itemId);
}

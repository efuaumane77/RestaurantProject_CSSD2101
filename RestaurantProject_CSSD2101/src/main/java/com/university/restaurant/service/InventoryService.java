package com.university.restaurant.service;

import com.university.restaurant.chain.inventory.InventoryPermissionChain;
import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.InventoryServicePort;
import com.university.restaurant.repository.InventoryRepository;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

/**
 * <p>
 * Service implementation for inventory-related operations such as reducing or
 * increasing stock levels and querying current inventory state. This class
 * enforces business rules, interacts with repositories, and ensures that
 * menu item availability is synchronized with stock levels.
 * </p>
 *
 * <p>
 * Role-based authorization is handled through the
 * {@link InventoryPermissionChain}, which determines whether a given
 * {@link StaffRole} has permission to perform inventory operations.
 * </p>
 *
 * <p>
 * All mutations to inventory are logged using {@link RestaurantAuditLogRepository}
 * to provide a tamper-evident audit trail.
 * </p>
 */
public class InventoryService implements InventoryServicePort {

    /** Repository providing access to persisted inventory items. */
    private final InventoryRepository repo;

    /** Repository containing menu items affected by inventory changes. */
    private final MenuRepository menuRepo;

    /** Audit log repository used to track inventory actions. */
    private final RestaurantAuditLogRepository audits;

    /** Permission chain used to validate actor roles for inventory operations. */
    private final InventoryPermissionChain permissions = new InventoryPermissionChain();

    /**
     * Constructs the inventory service with the required repositories and audit log.
     *
     * @param r        the inventory repository
     * @param menuRepo the menu repository for updating item availability
     * @param a        the audit log repository
     */
    public InventoryService(InventoryRepository r, MenuRepository menuRepo, RestaurantAuditLogRepository a) {
        this.repo = r;
        this.menuRepo = menuRepo;
        this.audits = a;
    }

    /**
     * <p>
     * Reduces the stock level of a specific inventory item. If the stock reaches zero,
     * the corresponding menu item (if present) is marked unavailable.
     * </p>
     *
     * <p>
     * Steps performed:
     * </p>
     * <ol>
     *     <li>Verify actor permissions.</li>
     *     <li>Load the inventory item or throw if not found.</li>
     *     <li>Consume the specified quantity (with domain rules enforced).</li>
     *     <li>Persist the updated item.</li>
     *     <li>If stock reaches zero, update the related menu item's availability.</li>
     *     <li>Record an audit log entry.</li>
     * </ol>
     *
     * @param actor  the staff role performing the action
     * @param itemId the ID of the inventory item
     * @param qty    the quantity to reduce
     *
     * @throws SecurityException        if the actor lacks permission
     * @throws IllegalArgumentException if the item does not exist
     * @throws IllegalStateException    if the reduction violates stock constraints
     */
    @Override
    public void reduceStock(StaffRole actor, String itemId, int qty) {

        permissions.check(actor, "reduce stock");

        InventoryItem item = repo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Reduce stock
        item.consume(qty);

        // Save updated item
        repo.save(item);

        // If stock hits zero → mark MenuItem unavailable
        if (item.getStockLevel() == 0) {
            menuRepo.findById(itemId).ifPresent(menuItem -> {
                menuItem.setAvailable(false);
                menuRepo.save(menuItem);
            });
        }

        // Audit
        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "REDUCE_STOCK",
                "InventoryItem",
                itemId,
                "Reduced stock by " + qty,
                audits.tailHash()
        ));
    }

    /**
     * <p>
     * Increases the stock level of an inventory item. Capacity limits are enforced
     * by the domain logic within the {@link InventoryItem}. If stock becomes
     * positive, related menu items may be marked available again.
     * </p>
     *
     * @param actor  the staff role performing the restock
     * @param itemId the ID of the inventory item
     * @param qty    the quantity to add
     *
     * @throws SecurityException        if actor lacks permission
     * @throws IllegalArgumentException if the item does not exist
     */
    @Override
    public void increaseStock(StaffRole actor, String itemId, int qty) {

        permissions.check(actor, "increase stock");

        InventoryItem item = repo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Domain rule: restock with capacity enforcement
        item.restock(qty);

        // Save updated item
        repo.save(item);

        if (item.getStockLevel() > 0) {
            menuRepo.findById(itemId).ifPresent(menuItem -> {
                menuItem.setAvailable(true);
                menuRepo.save(menuItem);
            });
        }

        // Audit
        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "RESTOCK",
                "InventoryItem",
                itemId,
                "Restocked " + qty + " units",
                audits.tailHash()
        ));

    }

    /**
     * Retrieves the current stock level for a given inventory item.
     *
     * @param itemId the ID of the item to query
     * @return the current stock level
     *
     * @throws IllegalArgumentException if the item does not exist
     */
    @Override
    public int getStockLevel(String itemId) {
        InventoryItem item = repo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        return item.getStockLevel();
    }
}

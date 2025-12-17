package com.university.restaurant.service;

import com.university.restaurant.chain.menu.MenuPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.port.MenuServicePort;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.util.List;

/**
 * <p>
 * Service implementation for menu management operations such as adding new menu
 * items, updating prices, and retrieving available menu items. This class
 * implements the {@link MenuServicePort} and forms part of the application layer
 * under a Hexagonal Architecture.
 * </p>
 *
 * <p>
 * Role-based authorization is enforced via {@link MenuPermissionChain}, ensuring
 * that only permitted staff roles (typically managers) may perform menu
 * modifications.
 * </p>
 *
 * <p>
 * All changes to menu items are recorded through a tamper-evident audit log
 * using {@link RestaurantAuditLogRepository}.
 * </p>
 */
public class MenuService implements MenuServicePort {

    /** Repository responsible for persisting and retrieving menu items. */
    private final MenuRepository repo;

    /** Audit log repository used to record menu changes. */
    private final RestaurantAuditLogRepository audits;

    /** Permission chain enforcing which staff roles may modify menu data. */
    private final MenuPermissionChain permissions = new MenuPermissionChain();

    /**
     * Constructs a new menu service with the required repositories.
     *
     * @param r       the menu repository
     * @param a       the audit log repository
     */
    public MenuService(MenuRepository r, RestaurantAuditLogRepository a) {
        this.repo = r;
        this.audits = a;
    }

    /**
     * <p>
     * Adds a new {@link MenuItem} to the system. Authorization is verified before
     * the item is persisted. An audit log entry is recorded for traceability.
     * </p>
     *
     * @param actor the staff role attempting to add a menu item
     * @param item  the menu item to add
     *
     * @throws SecurityException        if the actor lacks permission
     */
    @Override
    public void addMenuItem(StaffRole actor, MenuItem item) {

        permissions.check(actor, "add a menu item");

        repo.save(item);

        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "ADD_MENU_ITEM",
                "MenuItem",
                item.getId(),
                "Added " + item.getName(),
                audits.tailHash()
        ));
    }

    /**
     * <p>
     * Updates the price of an existing menu item. Since {@link MenuItem} may be
     * designed as an immutable domain object, this method creates a new instance
     * using {@code copyWithPrice} and replaces the original item in the repository.
     * </p>
     *
     * <p>
     * An audit entry is recorded to capture the update event.
     * </p>
     *
     * @param actor   the staff role attempting to update the price
     * @param itemId  the unique identifier of the menu item
     * @param newPrice the new price value
     *
     * @throws SecurityException         if the actor lacks permission
     * @throws IllegalArgumentException  if the specified item does not exist
     */
    @Override
    public void updatePrice(StaffRole actor, String itemId, double newPrice) {
        permissions.check(actor, "update menu price");

        MenuItem oldItem = repo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Create a new immutable instance with updated price
        MenuItem updated = oldItem.copyWithPrice(newPrice);

        repo.save(updated);

        audits.append(new RestaurantAuditEntry(
                actor.id(),
                actor.getClass().getSimpleName(),
                "UPDATE_PRICE",
                "MenuItem",
                itemId,
                "Updated price to $" + newPrice,
                audits.tailHash()
        ));
    }

    /**
     * Returns a list of all currently available menu items. Availability is
     * typically influenced by inventory levels and manual staff actions.
     *
     * @return a list of menu items where {@link MenuItem#isAvailable()} is true
     */
    @Override
    public List<MenuItem> listMenuAvailableItems() {
        return repo.search(MenuItem::isAvailable);
    }
}

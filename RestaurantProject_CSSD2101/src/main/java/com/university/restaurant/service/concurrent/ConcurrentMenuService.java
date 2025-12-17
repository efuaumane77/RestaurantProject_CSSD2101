package com.university.restaurant.service.concurrent;

import com.university.restaurant.chain.menu.MenuPermissionChain;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.MenuServicePort;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.util.List;
import java.util.concurrent.locks.StampedLock;

/**
 * Thread-safe MenuService using StampedLock for menu updates.
 */
public class ConcurrentMenuService implements MenuServicePort {

    private final MenuRepository repo;
    private final RestaurantAuditLogRepository audits;
    private final MenuPermissionChain permissions = new MenuPermissionChain();
    private final StampedLock lock = new StampedLock();

    public ConcurrentMenuService(MenuRepository r, RestaurantAuditLogRepository a) {
        this.repo = r;
        this.audits = a;
    }

    @Override
    public void addMenuItem(StaffRole actor, MenuItem item) {
        permissions.check(actor, "add a menu item");

        long stamp = lock.writeLock();
        try {
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
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void updatePrice(StaffRole actor, String itemId, double newPrice) {
        permissions.check(actor, "update menu price");

        long stamp = lock.writeLock();
        try {
            MenuItem oldItem = repo.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

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
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public List<MenuItem> listMenuAvailableItems() {
        // Optimistic read for query
        long stamp = lock.tryOptimisticRead();
        List<MenuItem> items = repo.search(MenuItem::isAvailable);
        
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                items = repo.search(MenuItem::isAvailable);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        return items;
    }
}

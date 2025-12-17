package com.university.restaurant.service.concurrent;

import com.university.restaurant.chain.inventory.InventoryPermissionChain;
import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.staff.StaffRole;
import com.university.restaurant.port.InventoryServicePort;
import com.university.restaurant.repository.InventoryRepository;
import com.university.restaurant.repository.MenuRepository;
import com.university.restaurant.repository.RestaurantAuditEntry;
import com.university.restaurant.repository.RestaurantAuditLogRepository;

import java.util.concurrent.locks.StampedLock;

/**
 * Thread-safe implementation of InventoryService using StampedLock.
 * Provides optimistic reads for queries and write locks for mutations.
 */
public class ConcurrentInventoryService implements InventoryServicePort {

    private final InventoryRepository repo;
    private final MenuRepository menuRepo;
    private final RestaurantAuditLogRepository audits;
    private final InventoryPermissionChain permissions = new InventoryPermissionChain();
    
    // StampedLock for inventory operations
    private final StampedLock lock = new StampedLock();

    public ConcurrentInventoryService(InventoryRepository r, MenuRepository menuRepo, 
                                     RestaurantAuditLogRepository a) {
        this.repo = r;
        this.menuRepo = menuRepo;
        this.audits = a;
    }

    @Override
    public void reduceStock(StaffRole actor, String itemId, int qty) {
        permissions.check(actor, "reduce stock");

        // Acquire write lock for mutation
        long stamp = lock.writeLock();
        try {
            InventoryItem item = repo.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.consume(qty);
            repo.save(item);

            // Update menu availability if needed
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
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void increaseStock(StaffRole actor, String itemId, int qty) {
        permissions.check(actor, "increase stock");

        long stamp = lock.writeLock();
        try {
            InventoryItem item = repo.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

            item.restock(qty);
            repo.save(item);

            if (item.getStockLevel() > 0) {
                menuRepo.findById(itemId).ifPresent(menuItem -> {
                    menuItem.setAvailable(true);
                    menuRepo.save(menuItem);
                });
            }

            audits.append(new RestaurantAuditEntry(
                    actor.id(),
                    actor.getClass().getSimpleName(),
                    "RESTOCK",
                    "InventoryItem",
                    itemId,
                    "Restocked " + qty + " units",
                    audits.tailHash()
            ));
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int getStockLevel(String itemId) {
        // Try optimistic read first
        long stamp = lock.tryOptimisticRead();
        InventoryItem item = repo.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
        
        int stockLevel = item.getStockLevel();
        
        if (!lock.validate(stamp)) {
            // Optimistic read failed, fall back to read lock
            stamp = lock.readLock();
            try {
                item = repo.findById(itemId)
                        .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));
                stockLevel = item.getStockLevel();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        return stockLevel;
    }
}

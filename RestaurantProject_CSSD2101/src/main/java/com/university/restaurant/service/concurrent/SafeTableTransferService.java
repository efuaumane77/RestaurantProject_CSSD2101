package com.university.restaurant.service.concurrent;

import com.university.restaurant.model.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Example service showing deadlock-safe table transfers.
 */
public class SafeTableTransferService {
    
    private static final Logger log = LoggerFactory.getLogger(SafeTableTransferService.class);
    
    private final Map<Integer, Lock> tableLocks = new ConcurrentHashMap<>();
    private final DeadlockSafeReservationService safeReservationService;

    public SafeTableTransferService() {
        this.safeReservationService = new DeadlockSafeReservationService();
        
        // Initialize locks for tables 1-50
        for (int i = 1; i <= 50; i++) {
            tableLocks.put(i, new ReentrantLock());
        }
    }

    /**
     * Transfer reservation from one table to another safely.
     * Uses consistent lock ordering to prevent deadlock.
     */
    public boolean transferReservation(Reservation reservation, int fromTable, int toTable) {
        Lock fromLock = tableLocks.get(fromTable);
        Lock toLock = tableLocks.get(toTable);

        if (fromLock == null || toLock == null) {
            throw new IllegalArgumentException("Invalid table number");
        }

        // Use consistent lock ordering
        safeReservationService.withOrderedTableLocks(fromTable, toTable, fromLock, toLock, () -> {
            log.info("Transferring reservation {} from table {} to table {}", 
                    reservation.getId(), fromTable, toTable);
            // Perform actual transfer
            // reservation.setAssignedTable(toTable);
        });

        return true;
    }

    /**
     * Try to acquire table with timeout.
     */
    public boolean tryReserveTable(Reservation reservation, int tableNumber, 
                                   long timeoutSeconds) {
        Lock lock = tableLocks.get(tableNumber);
        if (lock == null) {
            throw new IllegalArgumentException("Invalid table number: " + tableNumber);
        }

        return safeReservationService.tryReserveWithTimeout(
            reservation, 
            lock, 
            timeoutSeconds, 
            TimeUnit.SECONDS
        );
    }

    /**
     * Reserve table with automatic retry.
     */
    public boolean reserveTableWithRetry(Reservation reservation, int tableNumber) {
        Lock lock = tableLocks.get(tableNumber);
        if (lock == null) {
            throw new IllegalArgumentException("Invalid table number: " + tableNumber);
        }

        return safeReservationService.reserveWithRetry(reservation, lock, 5);
    }

    /**
     * Reserve multiple tables atomically.
     * Either all tables are reserved or none are.
     */
    public boolean reserveMultipleTables(Reservation reservation, int[] tableNumbers) {
        Lock[] locks = new Lock[tableNumbers.length];
        
        // Get locks in ascending table number order (prevents deadlock)
        java.util.Arrays.sort(tableNumbers);
        
        for (int i = 0; i < tableNumbers.length; i++) {
            locks[i] = tableLocks.get(tableNumbers[i]);
            if (locks[i] == null) {
                throw new IllegalArgumentException("Invalid table number: " + tableNumbers[i]);
            }
        }

        return safeReservationService.tryLockAll(locks, 2, TimeUnit.SECONDS, () -> {
            log.info("Reserved tables {} for reservation {}", 
                    java.util.Arrays.toString(tableNumbers), 
                    reservation.getId());
            // Perform actual reservation
        });
    }
}

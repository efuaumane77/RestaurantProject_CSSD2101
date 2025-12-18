package com.university.restaurant.service.concurrent;

import com.university.restaurant.model.reservation.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Demonstrates deadlock-safe operations using lock ordering and timeouts.
 * Implements three key deadlock prevention strategies:
 * 1. Consistent lock ordering
 * 2. Lock timeouts
 * 3. Retry with exponential backoff
 */
public class DeadlockSafeReservationService {
    
    private static final Logger log = LoggerFactory.getLogger(DeadlockSafeReservationService.class);
    
    // Lock ordering comparator (consistent global order based on reservation ID)
    private static final Comparator<Reservation> LOCK_ORDER = 
        Comparator.comparing(r -> r.getId().toString());

    /**
     * Transfer reservation between tables using consistent lock ordering.
     * This prevents deadlock by always acquiring locks in the same order.
     * 
     * @param from First reservation
     * @param to Second reservation
     * @param fromLock Lock for first reservation
     * @param toLock Lock for second reservation
     */
    public void transferReservation(Reservation from, Reservation to, 
                                   Lock fromLock, Lock toLock) {
        // Determine lock acquisition order based on reservation IDs
        Lock firstLock, secondLock;
        
        if (LOCK_ORDER.compare(from, to) < 0) {
            // from has lower ID, acquire its lock first
            firstLock = fromLock;
            secondLock = toLock;
        } else {
            // to has lower ID, acquire its lock first
            firstLock = toLock;
            secondLock = fromLock;
        }

        // Acquire locks in consistent order
        firstLock.lock();
        try {
            secondLock.lock();
            try {
                // Safe to modify both reservations - no deadlock possible
                log.info("Transferred reservation from {} to {}", from.getId(), to.getId());
                // Actual transfer logic would go here
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    /**
     * Try to acquire lock with timeout to avoid indefinite waiting.
     * Returns false if lock cannot be acquired within timeout period.
     * 
     * @param reservation The reservation to lock
     * @param lock The lock to acquire
     * @param timeout How long to wait
     * @param unit Time unit for timeout
     * @return true if lock acquired and operation succeeded, false otherwise
     */
    public boolean tryReserveWithTimeout(Reservation reservation, Lock lock, 
                                        long timeout, TimeUnit unit) {
        try {
            // Try to acquire lock with timeout
            if (lock.tryLock(timeout, unit)) {
                try {
                    // Perform reservation operation
                    log.info("Reserved {}", reservation.getId());
                    return true;
                } finally {
                    lock.unlock();
                }
            } else {
                // Timeout occurred
                log.warn("Timeout acquiring lock for reservation {}", reservation.getId());
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for lock: {}", reservation.getId());
            return false;
        }
    }

    /**
     * Retry mechanism with exponential backoff.
     * Attempts to acquire lock multiple times with increasing wait periods.
     * 
     * @param reservation The reservation to process
     * @param lock The lock to acquire
     * @param maxRetries Maximum number of retry attempts
     * @return true if operation succeeded, false if all retries exhausted
     */
    public boolean reserveWithRetry(Reservation reservation, Lock lock, int maxRetries) {
        int attempts = 0;
        long backoffMs = 100; // Start with 100ms

        while (attempts < maxRetries) {
            try {
                if (lock.tryLock(backoffMs, TimeUnit.MILLISECONDS)) {
                    try {
                        // Success!
                        log.info("Reserved {} after {} attempts", 
                                reservation.getId(), attempts + 1);
                        return true;
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted during retry for {}", reservation.getId());
                return false;
            }

            // Failed to acquire lock, prepare for retry
            attempts++;
            backoffMs *= 2; // Exponential backoff: 100ms, 200ms, 400ms, 800ms...
            
            log.warn("Retry attempt {} for reservation {} (waiting {}ms)", 
                    attempts, reservation.getId(), backoffMs);
        }

        // All retries exhausted
        log.error("Failed to reserve {} after {} attempts", 
                reservation.getId(), maxRetries);
        return false;
    }

    /**
     * Lock ordering helper for tables.
     * Always acquires table locks in ascending table number order.
     * 
     * @param table1Number First table number
     * @param table2Number Second table number
     * @param lock1 Lock for first table
     * @param lock2 Lock for second table
     * @param operation Runnable to execute while both locks are held
     */
    public void withOrderedTableLocks(int table1Number, int table2Number,
                                     Lock lock1, Lock lock2,
                                     Runnable operation) {
        Lock firstLock, secondLock;
        
        // Always acquire locks in ascending table number order
        if (table1Number < table2Number) {
            firstLock = lock1;
            secondLock = lock2;
        } else {
            firstLock = lock2;
            secondLock = lock1;
        }

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                operation.run();
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    /**
     * Try-lock with timeout for multiple resources.
     * Acquires all locks or none, preventing partial acquisition.
     * 
     * @param locks Array of locks to acquire
     * @param timeout Timeout for each lock acquisition
     * @param unit Time unit
     * @param operation Runnable to execute if all locks acquired
     * @return true if operation completed, false if any lock couldn't be acquired
     */
    public boolean tryLockAll(Lock[] locks, long timeout, TimeUnit unit, 
                             Runnable operation) {
        int acquiredCount = 0;
        
        try {
            // Try to acquire all locks
            for (Lock lock : locks) {
                if (lock.tryLock(timeout, unit)) {
                    acquiredCount++;
                } else {
                    // Failed to acquire this lock
                    log.warn("Failed to acquire lock {}", acquiredCount);
                    return false;
                }
            }

            // All locks acquired, perform operation
            operation.run();
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // Release all acquired locks in reverse order
            for (int i = acquiredCount - 1; i >= 0; i--) {
                locks[i].unlock();
            }
        }
    }
}

package com.university.restaurant.concurrent;

import com.university.restaurant.model.reservation.Customer;
import com.university.restaurant.model.reservation.Reservation;
import com.university.restaurant.service.concurrent.DeadlockSafeReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for deadlock prevention mechanisms.
 */
class DeadlockPreventionTest {

    private DeadlockSafeReservationService service;

    @BeforeEach
    void setUp() {
        service = new DeadlockSafeReservationService();
    }

    @Test
    void transferReservation_withConsistentLockOrdering_shouldNotDeadlock() throws InterruptedException, ExecutionException {
        Customer customer = new Customer("John", "555-1234", "john@example.com");
        Reservation res1 = new Reservation(customer, LocalDateTime.now().plusDays(1), 4);
        Reservation res2 = new Reservation(customer, LocalDateTime.now().plusDays(2), 2);

        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Thread 1: Transfer res1 -> res2
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100; i++) {
                service.transferReservation(res1, res2, lock1, lock2);
            }
        }, executor);

        // Thread 2: Transfer res2 -> res1 (reverse order)
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100; i++) {
                service.transferReservation(res2, res1, lock2, lock1);
            }
        }, executor);

        // Should complete without deadlock
        assertDoesNotThrow(() -> {
            CompletableFuture.allOf(task1, task2).get(10, TimeUnit.SECONDS);
        });

        executor.shutdown();
    }

    @Test
    void tryReserveWithTimeout_shouldTimeoutGracefully() throws InterruptedException {
        Customer customer = new Customer("Jane", "555-5678", "jane@example.com");
        Reservation reservation = new Reservation(customer, LocalDateTime.now().plusHours(2), 3);

        Lock lock = new ReentrantLock();
        
        // Acquire lock in main thread
        lock.lock();
        
        try {
            // Try to acquire from another thread with timeout
            CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> {
                return service.tryReserveWithTimeout(reservation, lock, 100, TimeUnit.MILLISECONDS);
            });

            // Should timeout and return false
            assertFalse(result.get(2, TimeUnit.SECONDS));
        } finally {
            lock.unlock();
        }
    }

    @Test
    void reserveWithRetry_shouldEventuallySucceed() throws ExecutionException, InterruptedException, TimeoutException {
        Customer customer = new Customer("Test", "555-0000", "test@example.com");
        Reservation reservation = new Reservation(customer, LocalDateTime.now().plusDays(1), 2);

        Lock lock = new ReentrantLock();

        // Hold lock briefly, then release
        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> {
            lock.lock();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        // Wait a bit for holder to acquire lock
        Thread.sleep(50);

        // Try to reserve with retry
        CompletableFuture<Boolean> result = CompletableFuture.supplyAsync(() -> {
            return service.reserveWithRetry(reservation, lock, 5);
        });

        // Should succeed after retries
        Boolean success = result.get(3, TimeUnit.SECONDS);
        assertTrue(success, "Should eventually succeed with retry mechanism");

        holder.get();
    }

    @Test
    void highContentionScenario_shouldHandleGracefully() throws InterruptedException {
        Customer customer = new Customer("Test", "555-0000", "test@example.com");
        int numThreads = 20;
        int reservationsPerThread = 10;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        Lock sharedLock = new ReentrantLock();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < reservationsPerThread; j++) {
                        Reservation res = new Reservation(
                            customer, 
                            LocalDateTime.now().plusDays(1), 
                            2
                        );
                        service.tryReserveWithTimeout(res, sharedLock, 500, TimeUnit.MILLISECONDS);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), 
            "All threads should complete without deadlock");
        executor.shutdown();
    }
}

package com.university.restaurant.concurrent;

import com.university.restaurant.model.inventory.InventoryItem;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.repository.InMemoryInventoryRepo;
import com.university.restaurant.repository.InMemoryMenuRepo;
import com.university.restaurant.repository.InMemoryRestaurantAuditRepo;
import com.university.restaurant.service.concurrent.ConcurrentInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency tests for InventoryService using StampedLock.
 */
class ConcurrentInventoryTest {

    private ConcurrentInventoryService service;
    private InMemoryInventoryRepo repo;
    private Manager manager;
    private static final int INITIAL_STOCK = 1000;

    @BeforeEach
    void setUp() {
        repo = new InMemoryInventoryRepo();
        service = new ConcurrentInventoryService(
            repo,
            new InMemoryMenuRepo(),
            new InMemoryRestaurantAuditRepo()
        );
        manager = new Manager("m1", "Alice");

        // Add test item
        InventoryItem item = new InventoryItem("item-1", "Test Item", "kg", INITIAL_STOCK, 10, 2000);
        repo.save(item);
    }

    @Test
    void concurrentReduceStock_shouldMaintainConsistency() throws InterruptedException, ExecutionException {
        int numThreads = 10;
        int reductionsPerThread = 10;
        int reductionAmount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        // Submit concurrent reduction tasks
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < reductionsPerThread; j++) {
                        service.reduceStock(manager, "item-1", reductionAmount);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        // Verify final stock level
        int expectedStock = INITIAL_STOCK - (numThreads * reductionsPerThread * reductionAmount);
        int actualStock = service.getStockLevel("item-1");
        
        assertEquals(expectedStock, actualStock, 
            "Stock level should be consistent after concurrent reductions");
    }

    @Test
    void concurrentIncreaseStock_shouldMaintainConsistency() throws InterruptedException {
        // First reduce stock to create room for increases
        service.reduceStock(manager, "item-1", 500);

        int numThreads = 10;
        int increasesPerThread = 10;
        int increaseAmount = 3;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < increasesPerThread; j++) {
                        service.increaseStock(manager, "item-1", increaseAmount);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        int expectedStock = 500 + (numThreads * increasesPerThread * increaseAmount);
        int actualStock = service.getStockLevel("item-1");
        
        assertEquals(expectedStock, actualStock);
    }

    @Test
    void mixedConcurrentOperations_shouldMaintainConsistency() throws InterruptedException {
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        ConcurrentHashMap<String, Integer> operations = new ConcurrentHashMap<>();
        operations.put("reduce", 0);
        operations.put("increase", 0);

        // Half threads reduce, half increase
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    if (threadNum % 2 == 0) {
                        service.reduceStock(manager, "item-1", 10);
                        operations.merge("reduce", 10, Integer::sum);
                    } else {
                        service.increaseStock(manager, "item-1", 10);
                        operations.merge("increase", 10, Integer::sum);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        int expectedStock = INITIAL_STOCK - operations.get("reduce") + operations.get("increase");
        int actualStock = service.getStockLevel("item-1");
        
        assertEquals(expectedStock, actualStock);
    }

    @Test
    void concurrentReads_shouldNotBlock() throws InterruptedException, ExecutionException {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        long startTime = System.currentTimeMillis();
        
        // Submit many concurrent reads
        CompletableFuture<?>[] futures = IntStream.range(0, numThreads)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                for (int j = 0; j < 100; j++) {
                    service.getStockLevel("item-1");
                }
            }, executor))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);
        
        long duration = System.currentTimeMillis() - startTime;
        executor.shutdown();

        // Optimistic reads should be very fast
        assertTrue(duration < 5000, "Concurrent reads should complete quickly with optimistic locking");
    }

    @Test
    void stressTest_highContention_shouldRemainConsistent() throws InterruptedException {
        int numThreads = 50;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        CyclicBarrier barrier = new CyclicBarrier(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // Wait for all threads to be ready
                    barrier.await();
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (threadNum % 3 == 0) {
                            service.reduceStock(manager, "item-1", 1);
                        } else if (threadNum % 3 == 1) {
                            service.increaseStock(manager, "item-1", 1);
                        } else {
                            service.getStockLevel("item-1");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();

        // Verify no corruption occurred
        int finalStock = service.getStockLevel("item-1");
        assertTrue(finalStock >= 0 && finalStock <= 2000, 
            "Stock should remain within valid bounds");
    }
}

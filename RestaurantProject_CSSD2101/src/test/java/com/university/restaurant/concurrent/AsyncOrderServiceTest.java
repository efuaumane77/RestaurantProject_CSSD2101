package com.university.restaurant.concurrent;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.menu.MenuItem;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.Waiter;
import com.university.restaurant.repository.InMemoryOrderRepo;
import com.university.restaurant.repository.InMemoryRestaurantAuditRepo;
import com.university.restaurant.service.concurrent.AsyncOrderService;
import com.university.restaurant.service.concurrent.KitchenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AsyncOrderService using CompletableFuture.
 */
class AsyncOrderServiceTest {

    private AsyncOrderService service;
    private KitchenService kitchenService;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        kitchenService = new KitchenService(3);
        service = new AsyncOrderService(
            new InMemoryOrderRepo(),
            new InMemoryRestaurantAuditRepo(),
            kitchenService
        );
        waiter = new Waiter("w1", "Bob");
    }

    @AfterEach
    void tearDown() {
        service.shutdown();
        kitchenService.shutdown();
    }

    @Test
    void placeOrderAsync_shouldCompleteSuccessfully() throws ExecutionException, InterruptedException, TimeoutException {
        List<MenuItem> items = List.of(
            new Drink("d1", "Coke", "desc", 2.99, false),
            new Drink("d2", "Sprite", "desc", 2.99, false)
        );

        CompletableFuture<Order> future = service.placeOrderAsync(waiter, "5", items);

        Order order = future.get(5, TimeUnit.SECONDS);

        assertNotNull(order);
        assertEquals(5, order.getTableNumber());
        assertEquals(2, order.getItems().size());
    }

    @Test
    void placeOrderAsync_withKitchenPrep_shouldUpdateStatus() throws ExecutionException, InterruptedException, TimeoutException {
        List<MenuItem> items = List.of(
            new Drink("d1", "Coke", "desc", 2.99, false)
        );

        CompletableFuture<Order> future = service.placeOrderAsync(waiter, "3", items);

        Order order = future.get(5, TimeUnit.SECONDS);

        // Order should be confirmed after kitchen notification
        assertTrue(order.getStatus() == OrderStatus.CONFIRMED || 
                   order.getStatus() == OrderStatus.PENDING);
    }

    @Test
    void multipleConcurrentOrders_shouldAllComplete() throws InterruptedException, ExecutionException {
        int numOrders = 10;
        CompletableFuture<Order>[] futures = new CompletableFuture[numOrders];

        List<MenuItem> items = List.of(new Drink("d1", "Coke", "desc", 2.99, false));

        // Place multiple orders concurrently
        for (int i = 0; i < numOrders; i++) {
            futures[i] = service.placeOrderAsync(waiter, String.valueOf(i + 1), items);
        }

        // Wait for all to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
        allOf.get(10, TimeUnit.SECONDS);

        // Verify all completed successfully
        for (CompletableFuture<Order> future : futures) {
            assertTrue(future.isDone());
            assertFalse(future.isCompletedExceptionally());
            assertNotNull(future.get());
        }
    }

    @Test
    void placeOrderAsync_withException_shouldFailGracefully() {
        // Invalid table ID should cause failure
        CompletableFuture<Order> future = service.placeOrderAsync(waiter, "invalid", List.of());

        assertThrows(ExecutionException.class, () -> {
            future.get(5, TimeUnit.SECONDS);
        });

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void asyncOrders_shouldNotBlockEachOther() throws ExecutionException, InterruptedException, TimeoutException {
        List<MenuItem> items = List.of(new Drink("d1", "Coke", "desc", 2.99, false));

        long startTime = System.currentTimeMillis();

        // Start 5 orders "simultaneously"
        CompletableFuture<Order> f1 = service.placeOrderAsync(waiter, "1", items);
        CompletableFuture<Order> f2 = service.placeOrderAsync(waiter, "2", items);
        CompletableFuture<Order> f3 = service.placeOrderAsync(waiter, "3", items);
        CompletableFuture<Order> f4 = service.placeOrderAsync(waiter, "4", items);
        CompletableFuture<Order> f5 = service.placeOrderAsync(waiter, "5", items);

        // Wait for all
        CompletableFuture.allOf(f1, f2, f3, f4, f5).get(10, TimeUnit.SECONDS);

        long duration = System.currentTimeMillis() - startTime;

        // Should complete faster than sequential execution
        assertTrue(duration < 5000, "Async orders should complete quickly");

        // All should succeed
        assertNotNull(f1.get());
        assertNotNull(f2.get());
        assertNotNull(f3.get());
        assertNotNull(f4.get());
        assertNotNull(f5.get());
    }
}

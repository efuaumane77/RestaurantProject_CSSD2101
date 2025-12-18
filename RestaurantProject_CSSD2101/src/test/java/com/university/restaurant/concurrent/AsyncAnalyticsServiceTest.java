package com.university.restaurant.concurrent;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.model.staff.Manager;
import com.university.restaurant.repository.InMemoryOrderRepo;
import com.university.restaurant.service.concurrent.AsyncAnalyticsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AsyncAnalyticsService.
 */
class AsyncAnalyticsServiceTest {

    private AsyncAnalyticsService service;
    private InMemoryOrderRepo orderRepo;
    private Manager manager;

    @BeforeEach
    void setUp() {
        orderRepo = new InMemoryOrderRepo();
        service = new AsyncAnalyticsService(orderRepo);
        manager = new Manager("m1", "Alice");

        // Add test data
        Order order1 = new Order(1, "w1");
        order1.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order1.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        order1.updateStatus(OrderStatus.PAID);
        orderRepo.save(order1);

        Order order2 = new Order(2, "w1");
        order2.addItem(new Drink("d2", "Sprite", "desc", 3.99, false));
        order2.updateStatus(OrderStatus.PAID);
        orderRepo.save(order2);
    }

    @AfterEach
    void tearDown() {
        service.shutdown();
    }

    @Test
    void computeTopSellingAsync_shouldReturnResults() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Map<String, Long>> future = service.computeTopSellingAsync(manager);

        Map<String, Long> topSelling = future.get(5, TimeUnit.SECONDS);

        assertNotNull(topSelling);
        assertEquals(2, topSelling.size());
        assertEquals(2L, topSelling.get("Coke"));
        assertEquals(1L, topSelling.get("Sprite"));
    }

    @Test
    void computeRevenueTodayAsync_shouldReturnTotal() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Double> future = service.computeRevenueTodayAsync(manager);

        Double revenue = future.get(5, TimeUnit.SECONDS);

        assertNotNull(revenue);
        assertEquals(9.97, revenue, 0.01);
    }

    @Test
    void multipleConcurrentAnalytics_shouldComplete() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Map<String, Long>> topSelling = service.computeTopSellingAsync(manager);
        CompletableFuture<Double> revenue = service.computeRevenueTodayAsync(manager);

        // Both should complete
        CompletableFuture.allOf(topSelling, revenue).get(10, TimeUnit.SECONDS);

        assertTrue(topSelling.isDone());
        assertTrue(revenue.isDone());
        assertNotNull(topSelling.get());
        assertNotNull(revenue.get());
    }

    @Test
    void asyncAnalytics_shouldNotBlockMainThread() {
        long startTime = System.currentTimeMillis();

        // Start computation
        CompletableFuture<Map<String, Long>> future = service.computeTopSellingAsync(manager);

        // Main thread should continue immediately
        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue(elapsed < 100, "Async call should return immediately");

        // Eventually completes
        assertDoesNotThrow(() -> future.get(5, TimeUnit.SECONDS));
    }
}

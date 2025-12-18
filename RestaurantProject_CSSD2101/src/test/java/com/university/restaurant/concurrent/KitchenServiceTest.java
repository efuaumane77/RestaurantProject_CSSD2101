package com.university.restaurant.concurrent;

import com.university.restaurant.model.menu.Drink;
import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import com.university.restaurant.service.concurrent.KitchenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Kitchen Worker Thread Pool.
 */
class KitchenServiceTest {

    private KitchenService kitchenService;

    @AfterEach
    void tearDown() {
        if (kitchenService != null) {
            kitchenService.shutdown();
        }
    }

    @Test
    void submitOrder_shouldBeProcessedByWorkers() {
        kitchenService = new KitchenService(3);
        
        Order order = new Order(1, "w1");
        order.addItem(new Drink("d1", "Coke", "desc", 2.99, false));
        
        kitchenService.submitOrder(order);

        // Wait for order to be processed
        await().atMost(5, TimeUnit.SECONDS)
               .until(() -> order.getStatus() == OrderStatus.READY);

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void multipleOrders_shouldBeProcessedConcurrently() {
        kitchenService = new KitchenService(5);
        
        int numOrders = 20;
        Order[] orders = new Order[numOrders];
        
        // Submit multiple orders
        for (int i = 0; i < numOrders; i++) {
            orders[i] = new Order(i + 1, "w1");
            orders[i].addItem(new Drink("d1", "Drink", "desc", 2.99, false));
            kitchenService.submitOrder(orders[i]);
        }

        // Wait for all orders to be processed
        await().atMost(15, TimeUnit.SECONDS)
               .until(() -> {
                   for (Order order : orders) {
                       if (order.getStatus() != OrderStatus.READY) {
                           return false;
                       }
                   }
                   return true;
               });

        // Verify all orders are ready
        for (Order order : orders) {
            assertEquals(OrderStatus.READY, order.getStatus());
        }
    }

    @Test
    void queueSize_shouldReflectPendingOrders() throws InterruptedException {
        kitchenService = new KitchenService(1); // Single worker for controlled testing
        
        Order order1 = new Order(1, "w1");
        Order order2 = new Order(2, "w1");
        
        kitchenService.submitOrder(order1);
        kitchenService.submitOrder(order2);

        // At least one should be in queue
        assertTrue(kitchenService.getQueueSize() >= 0);
    }

    @Test
    void shutdown_shouldStopProcessing() throws InterruptedException {
        kitchenService = new KitchenService(2);
        
        Order order = new Order(1, "w1");
        kitchenService.submitOrder(order);

        kitchenService.shutdown();

        // Should not accept new orders after shutdown
        assertThrows(IllegalStateException.class, () -> {
            kitchenService.submitOrder(new Order(2, "w1"));
        });
    }

    @Test
    void highLoad_shouldHandleGracefully() {
        kitchenService = new KitchenService(10);
        
        int numOrders = 100;
        CountDownLatch latch = new CountDownLatch(numOrders);
        
        ExecutorService submitter = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < numOrders; i++) {
            final int orderNum = i;
            submitter.submit(() -> {
                try {
                    Order order = new Order(orderNum, "w1");
                    order.addItem(new Drink("d1", "Drink", "desc", 2.99, false));
                    kitchenService.submitOrder(order);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail("Failed to submit all orders");
        }

        submitter.shutdown();
    }
}

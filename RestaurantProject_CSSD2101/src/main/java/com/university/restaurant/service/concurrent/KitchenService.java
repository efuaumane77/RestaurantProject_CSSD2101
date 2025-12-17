package com.university.restaurant.service.concurrent;

import com.university.restaurant.model.order.Order;
import com.university.restaurant.model.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Kitchen service managing a thread pool of workers to process orders.
 * Uses BlockingQueue for thread-safe order distribution.
 */
public class KitchenService {
    
    private static final Logger log = LoggerFactory.getLogger(KitchenService.class);
    
    private final ExecutorService workerPool;
    private final BlockingQueue<Order> orderQueue;
    private final int numWorkers;
    private volatile boolean shutdown = false;

    public KitchenService(int numWorkers) {
        this.numWorkers = numWorkers;
        this.orderQueue = new LinkedBlockingQueue<>();
        this.workerPool = Executors.newFixedThreadPool(numWorkers);
        
        // Start workers
        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i + 1;
            workerPool.submit(new KitchenWorker(workerId, orderQueue));
        }
        
        log.info("Kitchen service started with {} workers", numWorkers);
    }

    /**
     * Submit an order to the kitchen queue.
     */
    public void submitOrder(Order order) {
        if (shutdown) {
            throw new IllegalStateException("Kitchen service is shut down");
        }
        
        try {
            orderQueue.offer(order, 5, TimeUnit.SECONDS);
            log.info("Order {} submitted to kitchen queue", order.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to submit order", e);
        }
    }

    /**
     * Get current queue size.
     */
    public int getQueueSize() {
        return orderQueue.size();
    }

    /**
     * Shutdown the kitchen service gracefully.
     */
    public void shutdown() {
        shutdown = true;
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(30, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Kitchen service shut down");
    }

    /**
     * Kitchen worker runnable that processes orders from the queue.
     */
    private static class KitchenWorker implements Runnable {
        private final int workerId;
        private final BlockingQueue<Order> queue;
        private static final Logger log = LoggerFactory.getLogger(KitchenWorker.class);

        KitchenWorker(int workerId, BlockingQueue<Order> queue) {
            this.workerId = workerId;
            this.queue = queue;
        }

        @Override
        public void run() {
            log.info("Kitchen worker {} started", workerId);
            
            while (!Thread.interrupted()) {
                try {
                    Order order = queue.poll(1, TimeUnit.SECONDS);
                    if (order != null) {
                        processOrder(order);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("Kitchen worker {} interrupted", workerId);
                    break;
                }
            }
            
            log.info("Kitchen worker {} stopped", workerId);
        }

        private void processOrder(Order order) {
            log.info("Worker {} processing order {}", workerId, order.getId());
            
            try {
                // Simulate cooking time based on number of items
                int cookingTimeMs = order.getItems().size() * 500;
                Thread.sleep(cookingTimeMs);
                
                // Update order status
                order.updateStatus(OrderStatus.READY);
                
                log.info("Worker {} completed order {}", workerId, order.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Worker {} interrupted while processing order {}", workerId, order.getId());
            } catch (Exception e) {
                log.error("Worker {} failed to process order {}", workerId, order.getId(), e);
            }
        }
    }
}

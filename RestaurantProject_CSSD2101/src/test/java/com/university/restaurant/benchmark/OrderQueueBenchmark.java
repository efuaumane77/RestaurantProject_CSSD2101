package com.university.restaurant.benchmark;

import com.university.restaurant.model.order.Order;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;

/**
 * Benchmark for order queue throughput.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class OrderQueueBenchmark {

    private BlockingQueue<Order> linkedBlockingQueue;
    private BlockingQueue<Order> arrayBlockingQueue;
    private BlockingQueue<Order> linkedTransferQueue;

    @Setup(Level.Iteration)
    public void setup() {
        linkedBlockingQueue = new LinkedBlockingQueue<>(1000);
        arrayBlockingQueue = new ArrayBlockingQueue<>(1000);
        linkedTransferQueue = new LinkedTransferQueue<>();
    }

    @Benchmark
    @Group("linkedBlocking")
    @GroupThreads(4)
    public void linkedBlockingQueueProducer() throws InterruptedException {
        Order order = new Order(1, "waiter-1");
        linkedBlockingQueue.offer(order, 1, TimeUnit.MILLISECONDS);
    }

    @Benchmark
    @Group("linkedBlocking")
    @GroupThreads(4)
    public Order linkedBlockingQueueConsumer() throws InterruptedException {
        return linkedBlockingQueue.poll(1, TimeUnit.MILLISECONDS);
    }

    @Benchmark
    @Group("arrayBlocking")
    @GroupThreads(4)
    public void arrayBlockingQueueProducer() throws InterruptedException {
        Order order = new Order(1, "waiter-1");
        arrayBlockingQueue.offer(order, 1, TimeUnit.MILLISECONDS);
    }

    @Benchmark
    @Group("arrayBlocking")
    @GroupThreads(4)
    public Order arrayBlockingQueueConsumer() throws InterruptedException {
        return arrayBlockingQueue.poll(1, TimeUnit.MILLISECONDS);
    }

    @Benchmark
    @Group("linkedTransfer")
    @GroupThreads(4)
    public void linkedTransferQueueProducer() throws InterruptedException {
        Order order = new Order(1, "waiter-1");
        linkedTransferQueue.offer(order, 1, TimeUnit.MILLISECONDS);
    }

    @Benchmark
    @Group("linkedTransfer")
    @GroupThreads(4)
    public Order linkedTransferQueueConsumer() throws InterruptedException {
        return linkedTransferQueue.poll(1, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OrderQueueBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

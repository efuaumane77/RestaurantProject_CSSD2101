package com.university.restaurant.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Benchmark comparing StampedLock vs ReentrantLock performance.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class LockingBenchmark {

    private final StampedLock stampedLock = new StampedLock();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private int counter = 0;

    @Benchmark
    public int stampedLockWrite() {
        long stamp = stampedLock.writeLock();
        try {
            return ++counter;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    @Benchmark
    public int stampedLockOptimisticRead() {
        long stamp = stampedLock.tryOptimisticRead();
        int value = counter;
        
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                value = counter;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        
        return value;
    }

    @Benchmark
    public int reentrantLockWrite() {
        reentrantLock.lock();
        try {
            return ++counter;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Benchmark
    public int reentrantLockRead() {
        reentrantLock.lock();
        try {
            return counter;
        } finally {
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LockingBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}

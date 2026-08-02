package com.aer.core.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//Fixed-size pool with a bounded queue; rejects fast instead of growing unbounded.
public final class BoundedExecutorStrategy implements ExecutorStrategy {

    private final ThreadPoolExecutor pool;

    public BoundedExecutorStrategy(int coreSize, int maxSize, int queueCapacity) {
        this.pool = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                boundedThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @Override
    public ExecutorType type() {
        return ExecutorType.BOUNDED;
    }

    @Override
    public Executor executor() {
        return pool;
    }

    @Override
    public void shutdown() {
        pool.shutdown();
    }

    private static ThreadFactory boundedThreadFactory() {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> new Thread(runnable, "aer-bounded-" + counter.incrementAndGet());
    }
}

package com.aer.core.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;

//Dedicated, named ForkJoinPool for CPU-bound work; deliberately never the common pool.
public final class CpuExecutorStrategy implements ExecutorStrategy {

    private final ForkJoinPool pool;

    public CpuExecutorStrategy(int parallelism) {
        AtomicInteger counter = new AtomicInteger();
        ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory = p -> {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(p);
            thread.setName("aer-cpu-" + counter.incrementAndGet());
            return thread;
        };
        this.pool = new ForkJoinPool(parallelism, threadFactory, null, true);
    }

    @Override
    public ExecutorType type() {
        return ExecutorType.CPU;
    }

    @Override
    public Executor executor() {
        return pool;
    }

    @Override
    public void shutdown() {
        pool.shutdown();
    }
}

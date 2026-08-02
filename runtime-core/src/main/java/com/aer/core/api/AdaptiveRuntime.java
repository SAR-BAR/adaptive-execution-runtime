package com.aer.core.api;

import com.aer.core.executor.BoundedExecutorStrategy;
import com.aer.core.executor.CpuExecutorStrategy;
import com.aer.core.executor.ExecutorStrategyRegistry;
import com.aer.core.executor.VirtualThreadStrategy;
import com.aer.core.scheduling.TaskEventHandler;
import com.aer.core.submission.DisruptorBootstrap;
import com.aer.core.task.AdaptiveTask;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;


//The single public entry point an application uses.
public final class AdaptiveRuntime implements AutoCloseable {

    private final DisruptorBootstrap bootstrap;
    private final ExecutorStrategyRegistry registry;

    private AdaptiveRuntime(AdaptiveRuntimeConfig config) {
        this.registry = new ExecutorStrategyRegistry(
                new VirtualThreadStrategy(),
                new CpuExecutorStrategy(config.cpuExecutorParallelism()),
                new BoundedExecutorStrategy(
                        config.boundedExecutorCoreSize(),
                        config.boundedExecutorMaxSize(),
                        config.boundedExecutorQueueCapacity())
        );
        this.bootstrap = new DisruptorBootstrap(
                config.ringBufferSize(),
                toDisruptorWaitStrategy(config.waitStrategy()),
                new TaskEventHandler(registry));
        this.bootstrap.start();
    }

    public static AdaptiveRuntime start() {
        return new AdaptiveRuntime(AdaptiveRuntimeConfig.defaultConfig());
    }

    public static AdaptiveRuntime start(AdaptiveRuntimeConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        return new AdaptiveRuntime(config);
    }

    public <T> CompletableFuture<T> submit(AdaptiveTask<T> task) {
        Objects.requireNonNull(task, "task must not be null");
        CompletableFuture<Object> future = new CompletableFuture<>();
        bootstrap.publish(task, future);
        return (CompletableFuture<T>) future;
    }

    @Override
    public void close() {
        bootstrap.shutdown();
        registry.shutdownAll();
    }

    private static WaitStrategy toDisruptorWaitStrategy(AdaptiveRuntimeConfig.WaitStrategyChoice choice) {
        return switch (choice) {
            case BLOCKING -> new BlockingWaitStrategy();
            case YIELDING -> new YieldingWaitStrategy();
            case BUSY_SPIN -> new BusySpinWaitStrategy();
            case SLEEPING -> new SleepingWaitStrategy();
        };
    }
}

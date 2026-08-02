package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;

import java.util.EnumMap;
import java.util.Map;

//Holds all strategies; selects one per task via the "executor.hint" metadata tag.
public final class ExecutorStrategyRegistry {

    public static final String HINT_KEY = "executor.hint";
    private static final ExecutorType DEFAULT_TYPE = ExecutorType.VIRTUAL_THREAD;

    private final Map<ExecutorType, ExecutorStrategy> strategies;

    public ExecutorStrategyRegistry(VirtualThreadStrategy virtualThreadStrategy,
                                     CpuExecutorStrategy cpuExecutorStrategy,
                                     BoundedExecutorStrategy boundedExecutorStrategy) {
        this.strategies = new EnumMap<>(ExecutorType.class);
        strategies.put(ExecutorType.VIRTUAL_THREAD, virtualThreadStrategy);
        strategies.put(ExecutorType.CPU, cpuExecutorStrategy);
        strategies.put(ExecutorType.BOUNDED, boundedExecutorStrategy);
    }

    public ExecutorStrategy select(AdaptiveTask<?> task) {
        String hint = task.metadata().get(HINT_KEY);
        ExecutorType type = hint == null ? DEFAULT_TYPE : ExecutorType.fromLabel(hint);
        return strategies.get(type);
    }

    public void shutdownAll() {
        strategies.values().forEach(ExecutorStrategy::shutdown);
    }
}

package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;
import com.aer.core.task.TaskMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutorStrategyRegistryTest {

    private final VirtualThreadStrategy virtualThreadStrategy = new VirtualThreadStrategy();
    private final CpuExecutorStrategy cpuExecutorStrategy = new CpuExecutorStrategy(2);
    private final BoundedExecutorStrategy boundedExecutorStrategy = new BoundedExecutorStrategy(1, 2, 4);
    private final ExecutorStrategyRegistry registry =
            new ExecutorStrategyRegistry(virtualThreadStrategy, cpuExecutorStrategy, boundedExecutorStrategy);

    @AfterEach
    void tearDown() {
        registry.shutdownAll();
    }

    @Test
    void defaultsToVirtualThreadWhenNoHintIsPresent() {
        AdaptiveTask<String> task = AdaptiveTask.builder("no-hint", () -> "x").build();
        assertEquals(ExecutorType.VIRTUAL_THREAD, registry.select(task).type());
    }

    @Test
    void selectsCpuStrategyWhenHinted() {
        AdaptiveTask<String> task = AdaptiveTask.builder("cpu-hint", () -> "x")
                .metadata(TaskMetadata.of(Map.of(ExecutorStrategyRegistry.HINT_KEY, "cpu")))
                .build();
        assertEquals(ExecutorType.CPU, registry.select(task).type());
    }

    @Test
    void selectsBoundedStrategyWhenHinted() {
        AdaptiveTask<String> task = AdaptiveTask.builder("bounded-hint", () -> "x")
                .metadata(TaskMetadata.of(Map.of(ExecutorStrategyRegistry.HINT_KEY, "bounded")))
                .build();
        assertEquals(ExecutorType.BOUNDED, registry.select(task).type());
    }
}

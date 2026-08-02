package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpuExecutorStrategyTest {

    private final CpuExecutorStrategy strategy = new CpuExecutorStrategy(2);

    @AfterEach
    void tearDown() {
        strategy.shutdown();
    }

    @Test
    void runsOnADedicatedNamedWorkerThreadNotTheCommonPool() throws Exception {
        AdaptiveTask<String> task =
                AdaptiveTask.builder("thread-name", () -> Thread.currentThread().getName()).build();
        CompletableFuture<Object> future = new CompletableFuture<>();

        strategy.execute(task, future);

        String threadName = (String) future.get(2, TimeUnit.SECONDS);
        assertTrue(threadName.startsWith("aer-cpu-"));
        assertFalse(threadName.contains("commonPool"));
    }
}

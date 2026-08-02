package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualThreadStrategyTest {

    private final VirtualThreadStrategy strategy = new VirtualThreadStrategy();

    @AfterEach
    void tearDown() {
        strategy.shutdown();
    }

    @Test
    void runsTaskOnAVirtualThreadAndCompletesTheFuture() throws Exception {
        AdaptiveTask<Boolean> task =
                AdaptiveTask.builder("is-virtual", () -> Thread.currentThread().isVirtual()).build();
        CompletableFuture<Object> future = new CompletableFuture<>();

        strategy.execute(task, future);

        assertTrue((Boolean) future.get(2, TimeUnit.SECONDS));
    }

    @Test
    void failingTaskCompletesFutureExceptionally() {
        AdaptiveTask<Object> task = AdaptiveTask.builder("boom", () -> {
            throw new IllegalStateException("boom");
        }).build();
        CompletableFuture<Object> future = new CompletableFuture<>();

        strategy.execute(task, future);

        ExecutionException thrown = assertThrows(ExecutionException.class,
                () -> future.get(2, TimeUnit.SECONDS));
        assertInstanceOf(IllegalStateException.class, thrown.getCause());
    }
}

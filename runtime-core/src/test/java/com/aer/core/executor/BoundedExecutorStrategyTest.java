package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundedExecutorStrategyTest {

    private final BoundedExecutorStrategy strategy = new BoundedExecutorStrategy(1, 1, 1);

    @AfterEach
    void tearDown() {
        strategy.shutdown();
    }

    @Test
    void rejectsWithoutBlockingTheCallerWhenPoolAndQueueAreFull() throws Exception {
        CountDownLatch block = new CountDownLatch(1);
        AdaptiveTask<Object> blocker = AdaptiveTask.builder("blocker", () -> {
            block.await();
            return null;
        }).build();
        AdaptiveTask<Object> queued = AdaptiveTask.builder("queued", () -> null).build();
        AdaptiveTask<Object> rejected = AdaptiveTask.builder("rejected", () -> null).build();

        strategy.execute(blocker, new CompletableFuture<>());
        strategy.execute(queued, new CompletableFuture<>());

        CompletableFuture<Object> rejectedFuture = new CompletableFuture<>();
        long start = System.nanoTime();
        strategy.execute(rejected, rejectedFuture);
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(elapsedMillis < 500, "caller must not block waiting for capacity");

        ExecutionException thrown = assertThrows(ExecutionException.class,
                () -> rejectedFuture.get(2, TimeUnit.SECONDS));
        assertInstanceOf(ExecutorSaturatedException.class, thrown.getCause());

        block.countDown();
    }
}

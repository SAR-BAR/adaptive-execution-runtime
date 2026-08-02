package com.aer.core.api;

import com.aer.core.task.AdaptiveTask;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdaptiveRuntimeTest {

    @Test
    void submittedTaskCompletesWithItsResult() throws Exception {
        try (AdaptiveRuntime runtime = AdaptiveRuntime.start()) {
            AdaptiveTask<String> task = AdaptiveTask.builder("greet", () -> "hello").build();

            CompletableFuture<String> future = runtime.submit(task);

            assertEquals("hello", future.get(2, TimeUnit.SECONDS));
        }
    }

    @Test
    void failingTaskCompletesExceptionallyWithoutKillingTheConsumer() throws Exception {
        try (AdaptiveRuntime runtime = AdaptiveRuntime.start()) {
            AdaptiveTask<Object> failing = AdaptiveTask.builder("boom", () -> {
                throw new IllegalStateException("boom");
            }).build();

            CompletableFuture<Object> failedFuture = runtime.submit(failing);
            ExecutionException thrown = assertThrows(
                    ExecutionException.class, () -> failedFuture.get(2, TimeUnit.SECONDS));
            assertInstanceOf(IllegalStateException.class, thrown.getCause());

            // the consumer thread must still be alive and processing after a task throws
            AdaptiveTask<String> next = AdaptiveTask.builder("still-alive", () -> "yes").build();
            assertEquals("yes", runtime.submit(next).get(2, TimeUnit.SECONDS));
        }
    }

    @Test
    void manyConcurrentProducersEachGetTheirOwnCorrectResult() throws Exception {
        try (AdaptiveRuntime runtime = AdaptiveRuntime.start()) {
            List<CompletableFuture<Integer>> futures = IntStream.range(0, 2_000)
                    .parallel()
                    .mapToObj(i -> runtime.submit(AdaptiveTask.builder("square-" + i, () -> i * i).build()))
                    .toList();
            for (CompletableFuture<Integer> future : futures) {
                int value = future.get(2, TimeUnit.SECONDS);
                int root = (int) Math.sqrt(value);
                assertEquals(value, root * root);
            }
        }
    }

    @Test
    void taskBodyNeverRunsOnTheDisruptorConsumerThread() throws Exception {
        try (AdaptiveRuntime runtime = AdaptiveRuntime.start()) {
            AdaptiveTask<String> task =
                    AdaptiveTask.builder("thread-check", () -> Thread.currentThread().getName()).build();

            String threadName = runtime.submit(task).get(2, TimeUnit.SECONDS);

            assertFalse(threadName.startsWith("aer-scheduler-"));
        }
    }
}

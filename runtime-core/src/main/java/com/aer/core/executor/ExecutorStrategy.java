package com.aer.core.executor;

import com.aer.core.task.AdaptiveTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

//Dispatches a task onto its own executor; never blocks the calling thread.
public interface ExecutorStrategy {

    ExecutorType type();

    Executor executor();

    void shutdown();

    default void execute(AdaptiveTask<?> task, CompletableFuture<Object> future) {
        try {
            executor().execute(() -> runAndComplete(task, future));
        } catch (RejectedExecutionException e) {
            future.completeExceptionally(new ExecutorSaturatedException(type(), task.name(), e));
        }
    }

    private void runAndComplete(AdaptiveTask<?> task, CompletableFuture<Object> future) {
        try {
            future.complete(task.action().call());
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }
}

package com.aer.core.scheduling;

import com.aer.core.submission.TaskEvent;
import com.lmax.disruptor.EventHandler;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

//The Disruptor consumer.
public final class TaskEventHandler implements EventHandler<TaskEvent> {

    @Override
    public void onEvent(TaskEvent event, long sequence, boolean endOfBatch) {
        Callable<?> action = event.getTask().action();
        CompletableFuture<Object> future = event.getFuture();

        try {
            Object result = action.call();
            future.complete(result);
        } catch (Throwable t) {
            future.completeExceptionally(t);
        } finally {
            event.clear();
        }
    }
}

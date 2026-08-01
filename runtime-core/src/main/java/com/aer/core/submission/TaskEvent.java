package com.aer.core.submission;

import com.aer.core.task.AdaptiveTask;

import java.util.concurrent.CompletableFuture;


//The object that actually lives inside the Disruptor ring buffer.
public final class TaskEvent {

    private AdaptiveTask<?> task;
    private CompletableFuture<Object> future;
    private long enqueueNanos;

    public AdaptiveTask<?> getTask() {
        return task;
    }

    public void setTask(AdaptiveTask<?> task) {
        this.task = task;
    }

    public CompletableFuture<Object> getFuture() {
        return future;
    }

    public void setFuture(CompletableFuture<Object> future) {
        this.future = future;
    }

    public long getEnqueueNanos() {
        return enqueueNanos;
    }

    public void setEnqueueNanos(long enqueueNanos) {
        this.enqueueNanos = enqueueNanos;
    }

    //Drops references so a reused slot doesn't keep the previous task's object graph alive.
    public void clear() {
        this.task = null;
        this.future = null;
        this.enqueueNanos = 0L;
    }
}

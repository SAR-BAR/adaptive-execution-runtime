package com.aer.core.submission;

import com.aer.core.task.AdaptiveTask;
import com.lmax.disruptor.EventTranslatorTwoArg;

import java.util.concurrent.CompletableFuture;


public final class TaskEventTranslator
        implements EventTranslatorTwoArg<TaskEvent, AdaptiveTask<?>, CompletableFuture<Object>> {

    public static final TaskEventTranslator INSTANCE = new TaskEventTranslator();

    private TaskEventTranslator() {
    }

    @Override
    public void translateTo(TaskEvent event, long sequence, AdaptiveTask<?> task, CompletableFuture<Object> future) {
        event.setTask(task);
        event.setFuture(future);
        event.setEnqueueNanos(System.nanoTime());
    }
}

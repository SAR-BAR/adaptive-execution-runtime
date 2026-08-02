package com.aer.core.scheduling;

import com.aer.core.executor.ExecutorStrategy;
import com.aer.core.executor.ExecutorStrategyRegistry;
import com.aer.core.submission.TaskEvent;
import com.lmax.disruptor.EventHandler;

//The Disruptor consumer; dispatches only, never runs task bodies itself.
public final class TaskEventHandler implements EventHandler<TaskEvent> {

    private final ExecutorStrategyRegistry registry;

    public TaskEventHandler(ExecutorStrategyRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onEvent(TaskEvent event, long sequence, boolean endOfBatch) {
        ExecutorStrategy strategy = registry.select(event.getTask());
        strategy.execute(event.getTask(), event.getFuture());
        event.clear();
    }
}

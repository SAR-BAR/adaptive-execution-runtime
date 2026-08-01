package com.aer.core.submission;

import com.lmax.disruptor.EventFactory;

public final class TaskEventFactory implements EventFactory<TaskEvent> {

    @Override
    public TaskEvent newInstance() {
        return new TaskEvent();
    }
}

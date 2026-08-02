package com.aer.core.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//One virtual thread per task; best fit for blocking/IO-bound work.
public final class VirtualThreadStrategy implements ExecutorStrategy {

    private final ExecutorService executorService;

    public VirtualThreadStrategy() {
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public ExecutorType type() {
        return ExecutorType.VIRTUAL_THREAD;
    }

    @Override
    public Executor executor() {
        return executorService;
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}

package com.aer.core.executor;

//Signals a strategy's underlying executor rejected the task (pool + queue full).
public final class ExecutorSaturatedException extends RuntimeException {

    public ExecutorSaturatedException(ExecutorType type, String taskName, Throwable cause) {
        super("Executor [" + type.label() + "] rejected task [" + taskName + "]", cause);
    }
}

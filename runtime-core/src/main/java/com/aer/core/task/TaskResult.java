package com.aer.core.task;

import java.util.Objects;

//The outcome of one task execution.
public record TaskResult<T>(
        String taskId,
        String taskName,
        boolean success,
        T value,
        Throwable error,
        String executorName
) {

    public TaskResult {
        Objects.requireNonNull(taskId, "taskId must not be null");
        Objects.requireNonNull(taskName, "taskName must not be null");
        if (success && error != null) {
            throw new IllegalArgumentException("a successful result must not carry an error");
        }
        if (!success && error == null) {
            throw new IllegalArgumentException("a failed result must carry an error");
        }
    }

    public static <T> TaskResult<T> success(String taskId, String taskName, T value, String executorName) {
        return new TaskResult<>(taskId, taskName, true, value, null, executorName);
    }

    public static <T> TaskResult<T> failure(String taskId, String taskName, Throwable error, String executorName) {
        return new TaskResult<>(taskId, taskName, false, null, error, executorName);
    }
}

package com.aer.core.task;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;


 //A unit of work the application explicitly submits to the runtime.
public final class AdaptiveTask<T> {

    private final String id;
    private final String name;
    private final TaskPriority priority;
    private final TaskMetadata metadata;
    private final Callable<T> action;

    private AdaptiveTask(Builder<T> builder) {
        this.id = UUID.randomUUID().toString();
        this.name = builder.name;
        this.priority = builder.priority;
        this.metadata = builder.metadata;
        this.action = builder.action;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public TaskPriority priority() {
        return priority;
    }

    public TaskMetadata metadata() {
        return metadata;
    }

    public Callable<T> action() {
        return action;
    }

    public static <T> Builder<T> builder(String name, Callable<T> action) {
        return new Builder<>(name, action);
    }

    @Override
    public String toString() {
        return "AdaptiveTask{id=%s, name=%s, priority=%s}".formatted(id, name, priority);
    }

    public static final class Builder<T> {
        private final String name;
        private final Callable<T> action;
        private TaskPriority priority = TaskPriority.NORMAL;
        private TaskMetadata metadata = TaskMetadata.empty();

        private Builder(String name, Callable<T> action) {
            this.name = Objects.requireNonNull(name, "name must not be null");
            this.action = Objects.requireNonNull(action, "action must not be null");
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }

        public Builder<T> priority(TaskPriority priority) {
            this.priority = Objects.requireNonNull(priority, "priority must not be null");
            return this;
        }

        public Builder<T> metadata(TaskMetadata metadata) {
            this.metadata = Objects.requireNonNull(metadata, "metadata must not be null");
            return this;
        }

        public AdaptiveTask<T> build() {
            return new AdaptiveTask<>(this);
        }
    }
}

package com.aer.core.task;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


//Developer-supplied tags attached to a task at submission time.
public final class TaskMetadata {

    private static final TaskMetadata EMPTY = new TaskMetadata(Map.of());

    private final Map<String, String> tags;

    private TaskMetadata(Map<String, String> tags) {
        this.tags = Collections.unmodifiableMap(new LinkedHashMap<>(tags));
    }

    public static TaskMetadata empty() {
        return EMPTY;
    }

    public static TaskMetadata of(Map<String, String> tags) {
        Objects.requireNonNull(tags, "tags must not be null");
        return tags.isEmpty() ? EMPTY : new TaskMetadata(tags);
    }

    public Map<String, String> tags() {
        return tags;
    }

    public String get(String key) {
        return tags.get(key);
    }

    @Override
    public String toString() {
        return "TaskMetadata" + tags;
    }
}

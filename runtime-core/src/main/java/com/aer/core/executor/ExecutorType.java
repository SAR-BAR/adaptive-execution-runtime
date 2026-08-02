package com.aer.core.executor;

//Canonical executor names; also the values used later by TaskExecutionRecord.
public enum ExecutorType {
    VIRTUAL_THREAD("virtual-thread"),
    CPU("cpu"),
    BOUNDED("bounded");

    private final String label;

    ExecutorType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static ExecutorType fromLabel(String label) {
        for (ExecutorType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown executor label: " + label);
    }
}

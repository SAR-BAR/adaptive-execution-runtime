package com.aer.core.api;


public record AdaptiveRuntimeConfig(
        int ringBufferSize,
        WaitStrategyChoice waitStrategy,
        int cpuExecutorParallelism,
        int boundedExecutorCoreSize,
        int boundedExecutorMaxSize,
        int boundedExecutorQueueCapacity
) {

    public AdaptiveRuntimeConfig {
        if (Integer.bitCount(ringBufferSize) != 1) {
            throw new IllegalArgumentException(
                    "ringBufferSize must be a power of two, was " + ringBufferSize);
        }
    }

    public static AdaptiveRuntimeConfig defaultConfig() {
        return new AdaptiveRuntimeConfig(1024, WaitStrategyChoice.BLOCKING, Runtime.getRuntime().availableProcessors(), 4, 16, 256
        );
    }


    public enum WaitStrategyChoice {
        BLOCKING,
        YIELDING,
        BUSY_SPIN,
        SLEEPING
    }
}

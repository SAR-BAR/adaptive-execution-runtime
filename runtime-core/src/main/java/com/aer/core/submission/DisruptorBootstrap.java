package com.aer.core.submission;

import com.aer.core.task.AdaptiveTask;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


 //Owns the Disruptor ring buffer: construction, startup, publishing, and shutdown.
public final class DisruptorBootstrap {

    private final Disruptor<TaskEvent> disruptor;
    private final RingBuffer<TaskEvent> ringBuffer;

    public DisruptorBootstrap(int ringBufferSize, WaitStrategy waitStrategy, EventHandler<TaskEvent> handler) {
        Objects.requireNonNull(waitStrategy, "waitStrategy must not be null");
        Objects.requireNonNull(handler, "handler must not be null");

        this.disruptor = new Disruptor<>(
                new TaskEventFactory(),
                ringBufferSize,
                schedulerThreadFactory(),
                ProducerType.MULTI,
                waitStrategy
        );

        this.disruptor.handleEventsWith(handler);
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public void start() {
        disruptor.start();
    }

    public void publish(AdaptiveTask<?> task, CompletableFuture<Object> future) {
        ringBuffer.publishEvent(TaskEventTranslator.INSTANCE, task, future);
    }

    public void shutdown() {
        disruptor.shutdown();
    }

    private static ThreadFactory schedulerThreadFactory() {
        AtomicInteger counter = new AtomicInteger();
        return runnable -> {
            Thread thread = new Thread(runnable, "aer-scheduler-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }
}

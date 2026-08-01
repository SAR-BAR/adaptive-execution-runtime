# Project Vision

Modern Java applications typically make **static execution decisions**.

Developers manually decide which concurrency primitive should execute a particular task:

- Virtual Threads
- ForkJoinPool
- ThreadPoolExecutor

These decisions are typically made during development and remain fixed throughout the application's lifetime. While this approach is simple and predictable, it cannot adapt to changing runtime conditions such as workload patterns, executor saturation, or resource contention.

## A Different Approach

This project explores whether an **application-level adaptive runtime** can make better execution decisions using continuous runtime observations instead of static configuration.

Rather than hardcoding an executor for every task, the runtime continuously observes task execution and builds behaviour profiles over time.

Future execution decisions will be informed by:

- Historical execution patterns
- Runtime performance metrics
- Executor pressure and saturation
- System resource utilization
- Latency and throughput objectives

The runtime **does not replace the JVM scheduler**.

Instead, it acts as an intelligent execution policy layer built on top of existing Java concurrency primitives, selecting the most appropriate execution strategy while allowing the JVM to continue handling low-level thread scheduling.

The long-term objective is to answer a fundamental engineering question:

> **Can runtime feedback produce better execution decisions than static executor selection?**
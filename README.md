# Java Virtual Threads

Examples and demos for **Java Virtual Threads** (Project Loom), stable since
Java 21, running on Java 27.

## Prerequisites

- JDK 21+ (tested with JDK 27). `java -version`
- Some demos use preview APIs (Structured Concurrency) and need
  `--enable-preview`.

## Build

```bash
./run.sh
```

Compiles all `*.java` into `out/` (preview features enabled).

## Run a demo

```bash
java --enable-preview -cp out <ClassName>
```

Available demos:

| Class | Topic |
|---|---|
| `VirtualThreadsIntro` | Creation, properties, factory |
| `VirtualThreadExecutors` | `newVirtualThreadPerTaskExecutor`, 50k tasks |
| `VirtualThreadPerformance` | Platform vs virtual throughput |
| `VirtualThreadPinning` | `synchronized` pinning vs `ReentrantLock` |
| `VirtualThreadThreadLocal` | `ThreadLocal` usage & cleanup |
| `ScopedValuesDemo` | `ScopedValue` (preferred over ThreadLocal) |
| `StructuredConcurrencyDemo` | `StructuredTaskScope` (preview) |
| `VirtualThreadSchedulerDemo` | Carrier threads & scheduler |
| `VirtualThreadLockContention` | Lock contention scalability |
| `VirtualThreadBatchProcessor` | Concurrent batch processing |
| `VirtualThreadWebServer` | HTTP server on virtual threads |
| `VirtualThreadCustomFactory` | Custom factory, naming, uncaught handler |
| `VirtualThreadCancellation` | Interruption & cancellation |
| `VirtualThreadCompletableFuture` | Virtual threads + CompletableFuture |

## Run all demos

```bash
./run-all.sh
```

## Pinning trace

To see pinned-thread stack traces:

```bash
java -Djdk.tracePinnedThreads=short --enable-preview -cp out VirtualThreadPinning
```

## Best practices

- Use virtual threads for I/O-bound, high-concurrency workloads.
- Do not pool virtual threads; create per task (`newVirtualThreadPerTaskExecutor`).
- Avoid `synchronized` around blocking calls (pins carrier); prefer
  `java.util.concurrent.locks`.
- Prefer `ScopedValue` over `ThreadLocal` for inherited context.
- Use structured concurrency for fan-out/fan-in with cancellation.
EOF
echo done
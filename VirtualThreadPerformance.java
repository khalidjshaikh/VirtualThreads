/**
 * Performance comparison: platform threads vs virtual threads for blocking work.
 *
 * Run with: java --enable-preview VirtualThreadPerformance
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class VirtualThreadPerformance {
    static final int TASKS = 20_000;
    static final int SLEEP_MS = 10;

    static long run(ExecutorService exec) throws Exception {
        AtomicLong sum = new AtomicLong();
        long t0 = System.nanoTime();
        try (exec) {
            for (int i = 0; i < TASKS; i++) {
                final int v = i;
                exec.submit(() -> {
                    try { Thread.sleep(SLEEP_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    sum.addAndGet(v);
                    return null;
                });
            }
        }
        return (System.nanoTime() - t0) / 1_000_000;
    }

    public static void main(String[] args) throws Exception {
        // Virtual threads
        long vt = run(Executors.newVirtualThreadPerTaskExecutor());
        System.out.println("Virtual threads:  " + TASKS + " tasks in " + vt + " ms");

        // Platform threads (limited pool) - will be much slower / may OOM if unbounded.
        int poolSize = 200;
        long pt = run(Executors.newFixedThreadPool(poolSize));
        System.out.println("Platform pool(" + poolSize + "): " + TASKS + " tasks in " + pt + " ms");
    }
}
/**
 * Virtual Thread Executors
 *
 * Executors.newVirtualThreadPerTaskExecutor() creates a new virtual thread
 * for every submitted task. This is the recommended way to run large numbers
 * of concurrent, mostly-blocking tasks.
 */
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadExecutors {
    static final int N = 50_000;

    public static void main(String[] args) throws Exception {
        System.out.println("Submitting " + N + " tasks to virtual-thread executor...");
        AtomicInteger done = new AtomicInteger();
        long t0 = System.nanoTime();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < N; i++) {
                final int id = i;
                exec.submit(() -> {
                    // Simulate a short blocking operation (e.g. I/O wait).
                    try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    done.incrementAndGet();
                    return id;
                });
            }
        } // close waits for all tasks
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("Completed " + done.get() + " tasks in " + ms + " ms");
        System.out.println("All tasks finished: " + (done.get() == N));
    }
}
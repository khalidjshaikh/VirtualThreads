/**
 * Lock contention scalability with virtual threads.
 *
 * Demonstrates many virtual threads sharing a ReentrantLock without pinning.
 */
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadLockContention {
    static final ReentrantLock LOCK = new ReentrantLock();
    static volatile long counter = 0;

    public static void main(String[] args) throws Exception {
        int n = 10_000;
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < n; i++) {
                exec.submit(() -> {
                    LOCK.lock();
                    try {
                        counter++;
                        // tiny critical section
                    } finally {
                        LOCK.unlock();
                    }
                    return null;
                });
            }
        }
        System.out.println("counter=" + counter + " (expected " + n + ")");
        System.out.println("DONE");
    }
}
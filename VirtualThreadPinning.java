/**
 * Pinning
 *
 * A virtual thread is "pinned" to its carrier thread when it blocks inside a
 * synchronized block/method or runs native code. While pinned it cannot be
 * unmounted, reducing scalability. Prefer java.util.concurrent locks.
 *
 * Run with tracing enabled to see pinning:
 *   java -Djdk.tracePinnedThreads=short --enable-preview VirtualThreadPinning
 */
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadPinning {
    static final Object MONITOR = new Object();
    static final ReentrantLock LOCK = new ReentrantLock();

    static void pinned() {
        synchronized (MONITOR) {
            try {
                // Blocking while holding a monitor pins the carrier thread.
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void notPinned() {
        LOCK.lock();
        try {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } finally {
            LOCK.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Pinned (synchronized) version:");
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 8; i++) exec.submit(VirtualThreadPinning::pinned);
        }
        System.out.println("Unpinned (ReentrantLock) version:");
        try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 8; i++) exec.submit(VirtualThreadPinning::notPinned);
        }
        System.out.println("DONE. Re-run with -Djdk.tracePinnedThreads=short to observe pinning stacks.");
    }
}